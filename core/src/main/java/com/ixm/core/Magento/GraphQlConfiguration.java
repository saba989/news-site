package com.ixm.core.Magento;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service =GraphQlConfiguration.class,immediate=true)
@Designate(ocd=GraphQlConfiguration.ServiceConfig.class)
public class GraphQlConfiguration {
String url;
	
	public String getUrl() {
		
		return url;
	}
	
	@Activate
	public void activate(GraphQlConfiguration.ServiceConfig serviceConfig) {
		url = serviceConfig.link();
	}
	
	@ObjectClassDefinition(name = "GraphQl Configuartion", description= "Configuring graphql endpoint url")
	public @interface ServiceConfig{
		@AttributeDefinition(name = "URL", type = AttributeType.STRING)
		public String link() default "http://10.127.126.134/graphql";
	}
}
