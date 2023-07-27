package com.ixm.core.migration.servlets;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.stubbing.*;

import com.ixm.core.migration.jobs.MigrationJob;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.testing.mock.aem.junit5.AemContext;

class MigrationServletTest {
	
private final AemContext aemContext = new AemContext();
	
	private MigrationServlet migrationServlet;
	private JobManager jobManager;

	@BeforeEach
	void setUp() throws Exception {
		jobManager = mock(JobManager.class);
		when(jobManager.addJob(anyString(), anyMap())).thenAnswer((Answer<Job>)invocation -> mock(Job.class));
		aemContext.registerService(JobManager.class, jobManager);
		migrationServlet = aemContext.registerInjectActivateService(new MigrationServlet());
	}

	@Test
	void testMethod() throws ServletException, IOException {
        final MockSlingHttpServletRequest request = aemContext.request();
        final MockSlingHttpServletResponse response = aemContext.response();		
        migrationServlet.doPost(request, response);
        verify(jobManager, times(1)).addJob(eq(MigrationJob.JOB_TOPIC), anyMap());
	}

}
