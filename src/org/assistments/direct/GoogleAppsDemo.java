package org.assistments.direct;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.assistments.connector.service.ProblemSetService;
import org.assistments.connector.service.ShareLinkService;
import org.assistments.connector.service.impl.ProblemSetServiceImpl;
import org.assistments.connector.service.impl.ShareLinkServiceImpl;
import org.assistments.connector.utility.Utils;
import org.assistments.dao.ConnectionFactory;
import org.assistments.service.domain.FolderItem;
import org.assistments.service.domain.ProblemSet;
import org.assistments.service.domain.ShareLink;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.sites.SiteEntry;
import com.google.gdata.data.sites.SitesAclFeedLink;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.XmlBlob;

@Controller
public class GoogleAppsDemo {

	@RequestMapping(value = "/google_apps", method = RequestMethod.GET)
	public String init(HttpSession session) {
		//load share links for this teacher
		String[] problemSets = {
				"PSAP7AH", "PSACQ9T", "PSACQ92", "PSAQYSY", "PSAQBPQ"
				};
		
		ProblemSetService pss = new ProblemSetServiceImpl();
		ShareLinkService sls = new ShareLinkServiceImpl(LiteUtility.PARTNER_REF);
		
		String userId = "260765";
		List<Map<String, String>> problemSetsInfo = new ArrayList<>();
		
		for(int i=0; i < problemSets.length; i++) {
			Map<String, String> info = new HashMap<>();
			String strId = Utils.decodeProblemSetString(problemSets[i]);
			ProblemSet ps = pss.find(Integer.valueOf(strId));
			String shareLinkRef = sls.create(userId, String.valueOf(ps.getDecodedID()), "generic", true);

			String problemSetName = ps.getName();
			int problemSetId = ps.getDecodedID();
			info.put("id", strId);
			info.put("name", problemSetName);
			session.setAttribute("problem_set_name", problemSetName);
			session.setAttribute("problem_set", problemSetId);
			session.setAttribute("share_link_ref", shareLinkRef);
			problemSetsInfo.add(info);
		}
		
		session.setAttribute("shared_problem_sets", problemSetsInfo);
		return "google_apps";
	}
	
	@RequestMapping(value = "/google_sites", method = RequestMethod.GET)
	public String google_sites() {
		
		return "google_sites";
	}
	
	@RequestMapping(value="/create_google_sites", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> setup(@RequestParam Map<String, Object> req) 
			throws MalformedURLException, IOException, ServiceException {
		String accessToken = (String)req.get("access_token");
		
		GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
		
//		String user = "szhao@gedu.demo.assistmentstestbed.org";
		
		String domain = "gedu.demo.assistmentstestbed.org";
		String siteName = (String)req.get("site_name");
//		String siteName = "skill-builder-iii";
		String applicationName = "direct";
		
		SiteEntry siteEntry = new SiteEntry();
		siteEntry.setTitle(new PlainTextConstruct(siteName));
		siteEntry.setCanEdit(true);
		
//		SitesHelper helper = new SitesHelper(applicationName, domain, siteName);
		
		SitesService service = new SitesService(applicationName);
		service.setOAuth2Credentials(credential);
		
		siteEntry = service.insert(new URL(getSiteFeedUrl(domain)), siteEntry);
		siteName = siteEntry.getSiteName().getValue();

		AclRole role = new AclRole("writer");
		AclScope scope = new AclScope(AclScope.Type.DOMAIN, domain);
		
		AclEntry aclEntry = new AclEntry();
		aclEntry.setRole(role);
		aclEntry.setScope(scope);

		Link aclLink = siteEntry.getLink(SitesAclFeedLink.Rel.ACCESS_CONTROL_LIST, Link.Type.ATOM);
		service.insert(new URL(aclLink.getHref()), aclEntry);
		String folderId = (String)req.get("folder_id");
		ProblemSetService pss = new ProblemSetServiceImpl();
		ShareLinkService sls = new ShareLinkServiceImpl(LiteUtility.PARTNER_REF);
		List<Map<String, String>> folders = pss.getSubFoldersByFolderId(Integer.parseInt(folderId));
//		Integer[] folderIds = new Integer[]{186686, 177818, 177817, 177556, 177557, 177558, 177559, 177560, 177568, 198227, 198228};
//		Integer[] folderIds = new Integer[]{177558};
//		List<Integer> folderIdList = Arrays.asList(folderIds);
//		List<Map<String,String>> folders = pss.getFoldersByIds(folderIdList);
		
		Iterator<Map<String, String>> iter = folders.iterator();
		
		String linkType = (String)req.get("link_type");
		String assistmentsVerified = (String)req.get("assistments_verified");
		boolean isAssistmentsVerified = (assistmentsVerified.equals("true")) ? true : false;
		String userId = (String)req.get("owner_id");
		while(iter.hasNext()) {
			Map<String, String> map = iter.next();
			String folderName = map.get("name");
			String id = map.get("id");
			
			String content = "";
					
			List<FolderItem> items = pss.getFolderItemsByFolder(Long.valueOf(id));
			Iterator<FolderItem> tmpIter = items.iterator();
			while(tmpIter.hasNext()) {
				FolderItem tmpItem = tmpIter.next();
				switch(tmpItem.getType()) {
				case CURRICULUM_ITEM:
					ProblemSet ps = tmpItem.getPs();
					String shareLinkRef = new String();
					if(linkType.equals(ShareLink.GENERIC)){
						shareLinkRef = sls.create(userId, String.valueOf(ps.getDecodedID()), "generic", true);
					}else if(linkType.equals(ShareLink.VERIFIED)){
						String url = (String)req.get("url");
						String form = (String)req.get("form");
						shareLinkRef = sls.create(userId, String.valueOf(ps.getDecodedID()), "generic", true, url, form, isAssistmentsVerified);
					}
					
					String shareLink = LiteUtility.DIRECT_URL + "/share/" + shareLinkRef;
					String problemSetName = StringEscapeUtils.escapeXml11(ps.getName());
					content += "<div><a href='"+shareLink+"' target='_blank'>"+problemSetName+"</a></div>";
					break;
				case FOLDER:
					String sectionName = tmpItem.getName();
					sectionName = StringEscapeUtils.escapeXml11(sectionName);
					content += "<div><b>"+sectionName+"</b></div>";
					if(tmpItem.getChildren() != null) {
						Iterator<FolderItem> psItemIter = tmpItem.getChildren().iterator();
						while(psItemIter.hasNext()) {
							FolderItem fItem = psItemIter.next();
							ProblemSet ps1 = fItem.getPs();
							String shareLinkRef1 = new String();
							if(linkType.equals(ShareLink.GENERIC)){
								shareLinkRef1 = sls.create(userId, String.valueOf(ps1.getDecodedID()), "generic", true);
							}else if (linkType.equals(ShareLink.VERIFIED)){
								String url = (String)req.get("url");
								String form = (String)req.get("form");
								shareLinkRef1 =sls.create(userId, String.valueOf(ps1.getDecodedID()), "generic", true, url, form, isAssistmentsVerified);
							}
							
							String shareLink1 = LiteUtility.DIRECT_URL + "/share/" + shareLinkRef1;
							
							problemSetName = StringEscapeUtils.escapeXml11(ps1.getName());
	//						content += "<div style='margin-left: 30px;'><a href='"+shareLink1+"' target='_blank'>"+ps1.getName()+"</a></div>";
							content += "<div style='margin-left: 30px;'><a href='"+shareLink1+"' target='_blank'>"+problemSetName+"</a></div>";
						}
					}
					break;					
				}
			}
						
			WebPageEntry entry = new WebPageEntry();
			XmlBlob xml = new XmlBlob();
			if("".equals(content)) {
				content = "Sorry... We cannot find any problem set.";
			}
			xml.setBlob(content);
			entry.setContent(new XhtmlTextConstruct(xml));
			entry.setTitle(new PlainTextConstruct(folderName));
			
			service.insert(new URL(getContentFeedUrl(domain, siteName)), entry);
		}
		
		Map<String, Object> resp = new HashMap<>();
		return new ResponseEntity<Map<String,Object>>(resp, HttpStatus.OK);
	}
	
	@RequestMapping(value="/migration", method = RequestMethod.GET)
	public void migration() {
		try {
			Connection conn = ConnectionFactory.getInstance().getConnection();
			String sql = "alter table share_links alter column form Type character varying(256)";
			Statement pstmt = conn.createStatement();
			pstmt.execute(sql);
			
			sql = "alter table share_links alter column url Type character varying(256)";
			pstmt.execute(sql);
			
			sql = "ALTER TABLE share_links ADD COLUMN assistments_verified BOOLEAN";
			pstmt.execute(sql);
			
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			new RuntimeException(e);
		}
	}
	
	public String getContentFeedUrl(String domain, String siteName) {
	      return "https://sites.google.com/feeds/content/" + domain + "/" + siteName + "/";
	    }

	    public String getRevisionFeedUrl(String domain, String siteName) {
	      return "https://sites.google.com/feeds/revision/" + domain + "/" + siteName + "/";
	    }

	    public String getActivityFeedUrl(String domain, String siteName) {
	      return "https://sites.google.com/feeds/activity/" + domain + "/" + siteName + "/";
	    }

	    public String getSiteFeedUrl(String domain) {
	      return "https://sites.google.com/feeds/site/" + domain + "/";
	    }
	    
	    public String getAclFeedUrl(String domain, String siteName) {
	      return "https://sites.google.com/feeds/acl/site/" + domain + "/" + siteName + "/";
	    }
	    
	public static void main(String[] args) throws GeneralSecurityException, IOException, SitesException, ServiceException {
		try {
			Connection conn = ConnectionFactory.getInstance().getConnection();
			String sql = "alter table share_links alter column form Type character varying(256)";
			Statement pstmt = conn.createStatement();
			pstmt.execute(sql);
			
			sql = "alter table share_links alter column url Type character varying(256)";
			pstmt.execute(sql);
			
			sql = "ALTER TABLE share_links ADD COLUMN assistments_verified BOOLEAN";
			pstmt.execute(sql);
			
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			new RuntimeException(e);
		}
		System.out.println("Done");
	}
}
