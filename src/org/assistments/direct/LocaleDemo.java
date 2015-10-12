package org.assistments.direct;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocaleDemo {

	public static void main(String[] args) {
		ResourceBundle msg = ResourceBundle.getBundle("org.assistments.direct.Bundle", new Locale("zh", "CN"));
		
		System.out.println(msg.getString("logout"));
	}
}
