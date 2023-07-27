package com.ixm.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class UspItem {
	
	@Inject private String image;
	@Inject private String altText;
	@Inject private String uspTitle;
	@Inject private List<BulletItem> bullets;

	public String getImage() {
		return image;
	}

	public String getAltText() {
		return altText;
	}

	public String getUspTitle() {
		return uspTitle;
	}

	public List<BulletItem> getBullets() {
		if (null == bullets) {
			return null;
		}
		return Collections.unmodifiableList(bullets);
	}

}
