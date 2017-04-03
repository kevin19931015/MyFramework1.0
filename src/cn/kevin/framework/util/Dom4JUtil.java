package cn.kevin.framework.util;

import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class Dom4JUtil {
	private static InputStream in;
	static{
		in = Dom4JUtil.class.getClassLoader().getResourceAsStream("MyFramework.xml");
	}
	public static Document getDocument(){
		try {
			SAXReader reader = new SAXReader();
			return reader.read(in);
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}
}
