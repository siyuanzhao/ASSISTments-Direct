package org.assistments.direct;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.assistments.connector.service.ProblemSetService;
import org.assistments.connector.service.ShareLinkService;
import org.assistments.connector.service.impl.ProblemSetServiceImpl;
import org.assistments.connector.service.impl.ShareLinkServiceImpl;
import org.assistments.dao.ConnectionFactory;
import org.assistments.service.domain.FolderItem;
import org.assistments.service.domain.ProblemSet;
import org.assistments.service.domain.ShareLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.XhtmlTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.sites.BaseContentEntry;
import com.google.gdata.data.sites.ContentFeed;
import com.google.gdata.data.sites.SiteEntry;
import com.google.gdata.data.sites.SitesAclFeedLink;
import com.google.gdata.data.sites.WebPageEntry;
import com.google.gdata.util.InvalidEntryException;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.XmlBlob;
import com.google.gson.JsonObject;
@Controller
@RequestMapping(value = "/google_sites_api/v1")
public class GoogleSitesDirectAPI{
	@Autowired
	ServletContext servletContext;
	@RequestMapping(value = "/create", method = {RequestMethod.GET, RequestMethod.POST})
	public String beforeCreate(@RequestParam Map<String, Object> req, HttpSession session, RedirectAttributes redirectAttributes){
		System.out.println("here");
		System.out.println(req.get("owner_id").toString());
		servletContext.setAttribute("owner_id", req.get("owner_id"));
		servletContext.setAttribute("folder_id", req.get("folder_id"));
		servletContext.setAttribute("site_name", req.get("site_name"));
		servletContext.setAttribute("link_type", req.get("link_type"));
		servletContext.setAttribute("assistments_verified", req.get("assistments_verified"));
		servletContext.setAttribute("form", req.get("form"));
		servletContext.setAttribute("url", req.get("url"));
		servletContext.setAttribute("from", "create");
		return "redirect:/s/google_sites_api/v1/checkAuth";
//		redirectAttributes.addAttribute("owner_id", distributorId);
//		redirectAttributes.addAttribute("folder_id", req.get("folder_id"));
//		redirectAttributes.addAttribute("site_name", req.get("site_name"));
//		redirectAttributes.addAttribute("link_type", req.get("link_type"));
//		redirectAttributes.addAttribute("assistments_verified", req.get("assistments_verified"));
//		redirectAttributes.addAttribute("form", req.get("form"));
//		redirectAttributes.addAttribute("url", req.get("url"));
//		return "redirect:/s/google_sites_api/v1/create_sites";
	}
	
	@RequestMapping(value = "/create_sites", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<String> create(@RequestParam Map<String,Object> req, HttpSession session){
		String accessToken = (String)req.get("access_token");
		if(accessToken.equals("UNAUTHORIZED")){
			JsonObject json = new JsonObject();
			json.addProperty("message", "unauthorized");
			return new ResponseEntity<String>(json.toString(), HttpStatus.UNAUTHORIZED);
		}
		
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
		
		try {
			siteEntry = service.insert(new URL(getSiteFeedUrl(domain)), siteEntry);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidEntryException e) {
			// TODO Auto-generated catch block
			JsonObject json = new JsonObject();
			json.addProperty("message", "duplicate_site_name");
			return new ResponseEntity<String>(json.toString(), HttpStatus.BAD_REQUEST);
		} catch (ServiceException e){
			JsonObject json = new JsonObject();
			json.addProperty("message", "not_apps_account");
			return new ResponseEntity<String>(json.toString(), HttpStatus.FORBIDDEN);
		}
		siteName = siteEntry.getSiteName().getValue();

		AclRole role = new AclRole("writer");
		AclScope scope = new AclScope(AclScope.Type.DOMAIN, domain);
		
		AclEntry aclEntry = new AclEntry();
		aclEntry.setRole(role);
		aclEntry.setScope(scope);

		Link aclLink = siteEntry.getLink(SitesAclFeedLink.Rel.ACCESS_CONTROL_LIST, Link.Type.ATOM);
		try {
			service.insert(new URL(aclLink.getHref()), aclEntry);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			JsonObject json = new JsonObject();
			json.addProperty("message", "duplicate_site_name2");
			return new ResponseEntity<String>(json.toString(), HttpStatus.BAD_REQUEST);
		}
		String folderId = (String)req.get("folder_id");
		ProblemSetService pss = new ProblemSetServiceImpl();
		ShareLinkService sls = new ShareLinkServiceImpl(LiteUtility.PARTNER_REF);
		List<Map<String, String>> folders = pss.getSubFoldersByFolderId(Integer.parseInt(folderId));
//		Integer[] folderIds = new Integer[]{186686, 177818, 177817, 177556, 177557, 177558, 177559, 177560, 177568, 198227, 198228};
//		Integer[] folderIds = new Integer[]{177558};
//		List<Integer> folderIdList = Arrays.asList(folderIds);
//		List<Map<String,String>> folders = pss.getFoldersByIds(folderIdList);
		
		Iterator<Map<String, String>> iter = folders.iterator();
		System.out.println((String)req.get("form"));
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
			
			try {
				service.insert(new URL(getContentFeedUrl(domain, siteName)), entry);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String sitesUrl = "https://sites.google.com/a/gedu.demo.assistmentstestbed.org/" + siteName + "/";
		JsonObject json = new JsonObject();
		json.addProperty("url", sitesUrl);
		return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
	}
	
	@RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
	public String beforeUpdate(@RequestParam Map<String, Object> req, HttpSession session, RedirectAttributes redirectAttributes){
		redirectAttributes.addAttribute("owner_id", req.get("owner_id"));
		redirectAttributes.addAttribute("folder_id", req.get("folder_id"));
		redirectAttributes.addAttribute("site_name", req.get("site_name"));
		redirectAttributes.addAttribute("link_type", req.get("link_type"));
		redirectAttributes.addAttribute("assistments_verified", req.get("assistments_verified"));
		redirectAttributes.addAttribute("form", req.get("form"));
		redirectAttributes.addAttribute("url", req.get("url"));
		redirectAttributes.addAttribute("from", "update");
		return "redirect:/s/google_sites_api/v1/checkAuth";
	}
	
	@RequestMapping(value="/update_sites", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<String> update(@RequestParam Map<String, Object> req, HttpSession session) throws IOException, ServiceException{
		
		String accessToken = (String)req.get("access_token");
		GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
		String domain = "gedu.demo.assistmentstestbed.org";
		String siteUrl = (String)req.get("site_url");
		siteUrl = siteUrl.substring(siteUrl.lastIndexOf(domain));
		SitesService service = new SitesService("direct");
		service.setOAuth2Credentials(credential);
		ContentFeed contentFeed = null;
		try {
			contentFeed = service.getFeed(
				    new URL("https://sites.google.com/feeds/content/"+siteUrl), ContentFeed.class);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e){
			JsonObject json = new JsonObject();
			json.addProperty("message", "not_apps_account");
			return new ResponseEntity<String>(json.toString(), HttpStatus.FORBIDDEN);
		}
		List<BaseContentEntry> webPages = contentFeed.getEntries();
		BaseContentEntry homeEntry = new WebPageEntry();
		for(BaseContentEntry baseContentEntry : webPages){
			String pageUrl = baseContentEntry.getHtmlLink().getHref();
			if(pageUrl.substring(pageUrl.lastIndexOf('/')).equals("/home")){
				homeEntry.setContent(baseContentEntry.getContent());
				homeEntry.setTitle(baseContentEntry.getTitle());
			}
			baseContentEntry.delete();
		}
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
						
			BaseContentEntry entry = new WebPageEntry();
			XmlBlob xml = new XmlBlob();
			if("".equals(content)) {
				content = "Sorry... We cannot find any problem set.";
			}
			xml.setBlob(content);
			entry.setContent(new XhtmlTextConstruct(xml));
			entry.setTitle(new PlainTextConstruct(folderName));
			service.insert(new URL("https://sites.google.com/feeds/content/"+siteUrl), entry);
		}
		service.insert(new URL("https://sites.google.com/feeds/content/"+siteUrl), homeEntry);
		
		JsonObject json = new JsonObject();
		String siteUrl2 = (String)req.get("site_url");
		json.addProperty("url", siteUrl2);
		return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
	}
	
	@RequestMapping(value="/checkAuth", method = {RequestMethod.POST, RequestMethod.GET})
	public String checkAuthorization(@RequestParam Map<String, Object> req, HttpSession session, RedirectAttributes redirectAttributes){
		String accessToken = (String)servletContext.getAttribute("access_token");
		if(accessToken == null) {
			accessToken = (String)req.get("access_token");
			servletContext.setAttribute("access_token", accessToken);
		}
		if(accessToken == null) {
//			redirectAttributes.addAttribute("owner_id", req.get("owner_id"));
//			redirectAttributes.addAttribute("folder_id", req.get("folder_id"));
//			redirectAttributes.addAttribute("site_name", req.get("site_name"));
//			redirectAttributes.addAttribute("link_type", req.get("link_type"));
//			redirectAttributes.addAttribute("assistments_verified", req.get("assistments_verified"));
//			redirectAttributes.addAttribute("form", req.get("form"));
//			redirectAttributes.addAttribute("url", req.get("url"));
//			redirectAttributes.addAttribute("from", req.get("from"));
			return "redirect:/GoogleSitesServelet";
		}
		switch(servletContext.getAttribute("from").toString()){
		case "create":
//			redirectAttributes.addAttribute("owner_id", servletContext.getAttribute("owner_id"));
//			redirectAttributes.addAttribute("folder_id", servletContext.getAttribute("folder_id"));
//			redirectAttributes.addAttribute("site_name", servletContext.getAttribute("site_name"));
//			redirectAttributes.addAttribute("link_type", servletContext.getAttribute("link_type"));
//			redirectAttributes.addAttribute("assistments_verified", servletContext.getAttribute("assistments_verified"));
//			redirectAttributes.addAttribute("form", servletContext.getAttribute("form"));
//			redirectAttributes.addAttribute("url", servletContext.getAttribute("url"));
			return "redirect:/s/google_sites_api/v1/create_sites";
		case "update":
			redirectAttributes.addAttribute("owner_id", servletContext.getAttribute("owner_id"));
			redirectAttributes.addAttribute("folder_id", servletContext.getAttribute("folder_id"));
			redirectAttributes.addAttribute("site_url", servletContext.getAttribute("site_name"));
			redirectAttributes.addAttribute("link_type", servletContext.getAttribute("link_type"));
			redirectAttributes.addAttribute("assistments_verified", servletContext.getAttribute("assistments_verified"));
			redirectAttributes.addAttribute("form", servletContext.getAttribute("form"));
			redirectAttributes.addAttribute("url", servletContext.getAttribute("url"));
			return "redirect:/s/google_sites_api/v1/update_sites";
		default:
			System.out.println("404");
			return "redirect:/404";
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

}
