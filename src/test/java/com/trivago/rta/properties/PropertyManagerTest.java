package com.trivago.rta.properties;

import com.trivago.rta.exceptions.CucablePluginException;
import com.trivago.rta.logging.CucableLogger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PropertyManagerTest {
    private PropertyManager propertyManager;
    private CucableLogger logger;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        logger = mock(CucableLogger.class);
        propertyManager = new PropertyManager(logger);
    }

    @Test
    public void featureWithoutScenarioLineNumberTest() {
        propertyManager.setSourceFeatures("my.feature");
        assertThat(propertyManager.getSourceFeatures(), is("my.feature"));
        assertThat(propertyManager.getScenarioLineNumber(), is(nullValue()));
    }

    @Test
    public void featureWithScenarioLineNumberTest() {
        propertyManager.setSourceFeatures("my.feature:123");
        assertThat(propertyManager.getSourceFeatures(), is("my.feature"));
        assertThat(propertyManager.getScenarioLineNumber(), is(123));
    }

    @Test
    public void featureWithInvalidScenarioLineNumberTest() {
        propertyManager.setSourceFeatures("my.feature:abc");
        assertThat(propertyManager.getSourceFeatures(), is("my.feature:abc"));
        assertThat(propertyManager.getScenarioLineNumber(), is(nullValue()));
    }

    @Test
    public void wrongIncludeTagFormatTest() throws Exception {
        expectedException.expect(CucablePluginException.class);
        expectedException.expectMessage("Include tag 'noAtInFront' does not start with '@'.");

        propertyManager.setSourceFeatures("-");
        propertyManager.setSourceRunnerTemplateFile("-");
        propertyManager.setGeneratedFeatureDirectory("-");
        propertyManager.setGeneratedRunnerDirectory("-");
        List<String> tags = new ArrayList<>();
        tags.add("noAtInFront");
        propertyManager.setIncludeScenarioTags(tags);
        propertyManager.validateSettings();
    }

    @Test
    public void wrongExcludeTagFormatTest() throws Exception {
        expectedException.expect(CucablePluginException.class);
        expectedException.expectMessage("Exclude tag 'noAtInFront' does not start with '@'.");

        propertyManager.setSourceFeatures("-");
        propertyManager.setSourceRunnerTemplateFile("-");
        propertyManager.setGeneratedFeatureDirectory("-");
        propertyManager.setGeneratedRunnerDirectory("-");
        List<String> tags = new ArrayList<>();
        tags.add("noAtInFront");
        propertyManager.setExcludeScenarioTags(tags);
        propertyManager.validateSettings();
    }

    @Test
    public void logMandatoryPropertiesTest() {
        propertyManager.logProperties();
        verify(logger, times(5)).info(anyString());
    }

    @Test
    public void logExtendedPropertiesTest() {
        List<String> excludeScenarioTags = new ArrayList<>();
        excludeScenarioTags.add("exclude1");
        excludeScenarioTags.add("exclude2");
        propertyManager.setExcludeScenarioTags(excludeScenarioTags);

        List<String> includeScenarioTags = new ArrayList<>();
        includeScenarioTags.add("include1");
        includeScenarioTags.add("include2");
        propertyManager.setIncludeScenarioTags(includeScenarioTags);

        propertyManager.setSourceFeatures("test.feature:3");

        propertyManager.logProperties();
        verify(logger, times(8)).info(anyString());
    }
}
