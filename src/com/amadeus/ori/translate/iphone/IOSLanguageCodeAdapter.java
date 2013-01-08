package com.amadeus.ori.translate.iphone;

public class IOSLanguageCodeAdapter {

	/**
	 * Translates between ISO language codes and the ones used by IOS for iPhone and iPad
	 * @Link http://ykyuen.wordpress.com/2010/06/16/iphone-get-the-iphone-preferred-language-codes/
	 * @param code
	 * @return
	 */
	public static String adaptLanguageCode(String code){
		if (code.length() == 2) {
			return code;
		}

		if(code.equals("zh-tw")){
			return "zh-Hant";
		}

		if(code.equals("zh-cn")){
			return "zh-Hans";
		}		

		if(code.equals("pt-br")){
			return "pt";
		}
		
		return code;
	}
}
