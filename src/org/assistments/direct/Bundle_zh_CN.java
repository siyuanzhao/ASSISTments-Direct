package org.assistments.direct;

import java.util.ListResourceBundle;

public class Bundle_zh_CN extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return contents;
	}
	
	static final Object[][] contents = {
			{ "share.label1", "获取你的作业和报告链接" }, 
			{ "share.label2", "将我的链接发送到我的邮箱中" },
			{ "share.label3", "登录或创建帐户来获取我的链接" },
			{"share.label4", "为什么要创建一个帐户？<br>有帐户的老师可以在ASSISTmentsDirect.org管理他们的链接"},
			{"share.label5", "分享到Goolge Classroom"},
			{"share.view_problems", "查看所有题目"},
			{"share.banner", "这个题目集分享自"},
			{"share.powered_by", "基于"},
			{"share.wrong_password", "邮箱或者密码不正确！"},
			
			{ "email", "邮箱" }, 
			{ "password", "密码" },
			{ "sendButton", "发送" },
			{ "loginButton", "登录" },
			{ "createAccountButton", "创建帐户" },
			
			{ "teacher.home", "首页" }, 
			{ "teacher.account", "帐户" }, 
			{ "teacher.label1", "新创建的作业" },
			{ "teacher.assignments", "作业" },
			{ "teacher.problem_set_name", "题目集名字" },
			{ "teacher.view", "查看"},
			{ "teacher.share_link", "分享的链接"},
			{ "teacher.direct_links", "Direct链接"},
			
			{ "teacher.my_students", "学生列表" }, 
			{ "teacher_report_link", "老师报告链接" },
			{ "student_assignment_link", "学生作业链接" },
			{ "reset_password", "重置密码" },
			{ "logout", "退出" },
			
			{"student.label1", "请输入你的名字，然后点击\"进入作业\"来完成你的作业。"},
			{"student.first_name", "名"},
			{"student.last_name", "姓"},
			{"student.go_to_assignment", "进入作业"},
			
			{"teacher_login.incorrect_password", "邮箱或者密码不正确！"},
			{"teacher_login.failure_on_google", "不好意思，登录谷歌失败！"},
			{"teacher_login.account_not_found", "不好意思，我们在系统中没有找到相对应的帐户！"}
			
	};

}
