package cn.kevin.framework.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import cn.kevin.framework.util.Dom4JUtil;

public class CenterFilter implements Filter{
	private Map<String,Action>  actions= new HashMap<String,Action>();
	private FilterConfig filterConfig;
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		initCfg();
		this.filterConfig = filterConfig;
	}
	
	private void initCfg(){
		Document document = Dom4JUtil.getDocument();
		Element root = document.getRootElement();
		List<Element> actionElements = root.elements("action");
		if(actionElements!=null&&actionElements.size()>0){
			for(Element actionElement:actionElements){
				Action action = new Action();
				action.setName(actionElement.attributeValue("name"));
				action.setClassName(actionElement.attributeValue("class"));
				action.setMethod(actionElement.attributeValue("method"));
				
				List<Element> resultElements = actionElement.elements("result");
				if(resultElements!=null&&resultElements.size()>0){
					for(Element resultElement:resultElements){
						Result result = new Result();
						result.setType(ResultType.valueOf(resultElement.attributeValue("type")));
						result.setName(resultElement.attributeValue("name"));
						result.setTargetUri(resultElement.getText().trim());
						action.getResults().add(result);
					}
				}
				actions.put(action.getName(), action);
			}
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request= (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		try{
		String actionSuffixs[] = {"do","","action"};
		String actionSuffix = filterConfig.getInitParameter("actionSuffix");
		if(actionSuffix!=null){
			actionSuffixs=actionSuffix.split("\\,");
		}
		
		String uri = request.getRequestURI();
		String suffixName = uri.substring(uri.lastIndexOf(".")+1);
		
		boolean needProcess = false;
		for(String s:actionSuffixs){
			if(suffixName.equals(s)){
				needProcess=true;
				break;
			}
		}
		
		if(needProcess){
			String actionName = uri.substring(uri.lastIndexOf("/")+1,uri.lastIndexOf("."));
			if(actions.containsKey(actionName)){
				Action action = actions.get(actionName);
				Class clazz = Class.forName(action.getClassName());
				Object bean = clazz.newInstance();
				BeanUtils.populate(bean, request.getParameterMap());
				Method m = clazz.getMethod(action.getName(), null);
				String resultValue = (String) m.invoke(bean, null);
				
				List<Result> results = action.getResults();
				for(Result result:results){
					if(resultValue.equals(result.getName())){
						if("dispatcher".equals(result.getType().toString())){
							request.getRequestDispatcher(result.getTargetUri()).forward(request, response);
						}
						if("redirect".equals(result.getType().toString())){
							response.sendRedirect(request.getContextPath()+result.getTargetUri());
						}
					}
				}
			}else{
				throw new RuntimeException("The action "+actionName+" is not founded in your config files!");
			}
		}else{
			chain.doFilter(request, response);
		}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
