package com.sgoertzen.sonarbreak;

import com.sgoertzen.sonarbreak.qualitygate.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QueryExecutorTest {

    @Test
    public void buildURLTest() throws MalformedURLException {
        URL sonarURL = new URL("https://sonar.test.com");
        Query query = new Query("some-service", "1.0");
        URL url = QueryExecutor.buildURL(sonarURL, query);
        assertEquals("URL", "https://sonar.test.com/api/measures/component?componentKey=some-service&metricKeys=quality_gate_details", url.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildURLNoResourceTest() throws IllegalArgumentException, MalformedURLException {
        URL sonarURL = new URL("https://sonar.test.com");
        Query query = new Query("", "1.0");
        QueryExecutor.buildURL(sonarURL, query);
    }

    @Test
    public void parseResponseSimpleTest() throws Exception {
        String input = "{\"component\":{\"id\":\"AV1ryMPb5ewUHfWU_dSL\",\"key\":\"com.stuq.demo:skeleton-service\",\"name\":\"Skeleton Service\",\"qualifier\":\"TRK\",\"measures\":[{\"metric\":\"quality_gate_details\",\"value\":\"{\\\"level\\\":\\\"ERROR\\\",\\\"conditions\\\":[{\\\"metric\\\":\\\"new_security_rating\\\",\\\"op\\\":\\\"GT\\\",\\\"period\\\":1,\\\"error\\\":\\\"1\\\",\\\"actual\\\":\\\"1\\\",\\\"level\\\":\\\"OK\\\"},{\\\"metric\\\":\\\"new_reliability_rating\\\",\\\"op\\\":\\\"GT\\\",\\\"period\\\":1,\\\"error\\\":\\\"1\\\",\\\"actual\\\":\\\"4\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"new_maintainability_rating\\\",\\\"op\\\":\\\"GT\\\",\\\"period\\\":1,\\\"error\\\":\\\"1\\\",\\\"actual\\\":\\\"3\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"new_coverage\\\",\\\"op\\\":\\\"LT\\\",\\\"period\\\":1,\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"80\\\",\\\"actual\\\":\\\"13.461538461538462\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"code_smells\\\",\\\"op\\\":\\\"GT\\\",\\\"warning\\\":\\\"5\\\",\\\"error\\\":\\\"20\\\",\\\"actual\\\":\\\"84\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"vulnerabilities\\\",\\\"op\\\":\\\"GT\\\",\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"0\\\",\\\"actual\\\":\\\"5\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"skipped_tests\\\",\\\"op\\\":\\\"GT\\\",\\\"warning\\\":\\\"5\\\",\\\"error\\\":\\\"10\\\",\\\"actual\\\":\\\"0\\\",\\\"level\\\":\\\"OK\\\"},{\\\"metric\\\":\\\"coverage\\\",\\\"op\\\":\\\"LT\\\",\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"80\\\",\\\"actual\\\":\\\"31.7\\\",\\\"level\\\":\\\"ERROR\\\"}]}\"}]}}";

        Result result = QueryExecutor.parseResponse(input);
        assertEquals("AV1ryMPb5ewUHfWU_dSL", result.getId());
    }

    @Test(expected = SonarBreakException.class)
    public void parseResponseMissingMSRTest() throws Exception {
        String input = "{\"component\":{\"id\":\"AV1ryMPb5ewUHfWU_dSL\",\"key\":\"com.stuq.demo:skeleton-service\",\"name\":\"Skeleton Service\",\"qualifier\":\"TRK\"}}";

        Result result = QueryExecutor.parseResponse(input);
        assertEquals("AV1ryMPb5ewUHfWU_dSL", result.getId());
    }

    @Test
    public void parseResponseExtraFieldTest() throws Exception {
        String input = "{\"component\":{\"id\":\"AV1ryMPb5ewUHfWU_dSL\",\"key\":\"com.stuq.demo:skeleton-service\",\"name\":\"Skeleton Service\",\"measures\":[{\"metric\":\"quality_gate_details\",\"value\":\"{\\\"level\\\":\\\"ERROR\\\",\\\"conditions\\\":[{\\\"metric\\\":\\\"new_security_rating\\\",\\\"op\\\":\\\"GT\\\",\\\"period\\\":1,\\\"error\\\":\\\"1\\\",\\\"actual\\\":\\\"1\\\",\\\"level\\\":\\\"OK\\\"},{\\\"metric\\\":\\\"new_reliability_rating\\\",\\\"op\\\":\\\"GT\\\",\\\"period\\\":1,\\\"error\\\":\\\"1\\\",\\\"actual\\\":\\\"4\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"new_maintainability_rating\\\",\\\"op\\\":\\\"GT\\\",\\\"period\\\":1,\\\"error\\\":\\\"1\\\",\\\"actual\\\":\\\"3\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"new_coverage\\\",\\\"op\\\":\\\"LT\\\",\\\"period\\\":1,\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"80\\\",\\\"actual\\\":\\\"13.461538461538462\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"code_smells\\\",\\\"op\\\":\\\"GT\\\",\\\"warning\\\":\\\"5\\\",\\\"error\\\":\\\"20\\\",\\\"actual\\\":\\\"84\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"vulnerabilities\\\",\\\"op\\\":\\\"GT\\\",\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"0\\\",\\\"actual\\\":\\\"5\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"skipped_tests\\\",\\\"op\\\":\\\"GT\\\",\\\"warning\\\":\\\"5\\\",\\\"error\\\":\\\"10\\\",\\\"actual\\\":\\\"0\\\",\\\"level\\\":\\\"OK\\\"},{\\\"metric\\\":\\\"coverage\\\",\\\"op\\\":\\\"LT\\\",\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"80\\\",\\\"actual\\\":\\\"31.7\\\",\\\"level\\\":\\\"ERROR\\\"}]}\"}]}}";

        Result result = QueryExecutor.parseResponse(input);
        assertEquals("AV1ryMPb5ewUHfWU_dSL", result.getId());
    }

    @Test
    public void parseResponseConditionsTest() throws Exception {
        String input = "{\"component\":{\"id\":\"AV1ryMPb5ewUHfWU_dSL\",\"key\":\"com.stuq.demo:skeleton-service\",\"name\":\"Skeleton Service\",\"qualifier\":\"TRK\",\"measures\":[{\"metric\":\"quality_gate_details\",\"value\":\"{\\\"level\\\":\\\"ERROR\\\",\\\"conditions\\\":[{\\\"metric\\\":\\\"new_security_rating\\\",\\\"op\\\":\\\"GT\\\",\\\"period\\\":1,\\\"error\\\":\\\"1\\\",\\\"actual\\\":\\\"1\\\",\\\"level\\\":\\\"OK\\\"},{\\\"metric\\\":\\\"new_reliability_rating\\\",\\\"op\\\":\\\"GT\\\",\\\"period\\\":1,\\\"error\\\":\\\"1\\\",\\\"actual\\\":\\\"4\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"new_maintainability_rating\\\",\\\"op\\\":\\\"GT\\\",\\\"period\\\":1,\\\"error\\\":\\\"1\\\",\\\"actual\\\":\\\"3\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"new_coverage\\\",\\\"op\\\":\\\"LT\\\",\\\"period\\\":1,\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"80\\\",\\\"actual\\\":\\\"13.461538461538462\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"code_smells\\\",\\\"op\\\":\\\"GT\\\",\\\"warning\\\":\\\"5\\\",\\\"error\\\":\\\"20\\\",\\\"actual\\\":\\\"84\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"vulnerabilities\\\",\\\"op\\\":\\\"GT\\\",\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"0\\\",\\\"actual\\\":\\\"5\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"skipped_tests\\\",\\\"op\\\":\\\"GT\\\",\\\"warning\\\":\\\"5\\\",\\\"error\\\":\\\"10\\\",\\\"actual\\\":\\\"0\\\",\\\"level\\\":\\\"OK\\\"},{\\\"metric\\\":\\\"coverage\\\",\\\"op\\\":\\\"LT\\\",\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"80\\\",\\\"actual\\\":\\\"31.7\\\",\\\"level\\\":\\\"ERROR\\\"}]}\"}]}}";

        Result result = QueryExecutor.parseResponse(input);
        assertEquals("Level does not match", ConditionStatus.ERROR, result.getStatus());

        List<Condition> conditions = result.getConditions();
        assertNotNull("Conditions should not be null", conditions);
        assertEquals("Number of conditions does not match", 8, conditions.size());

        Condition condition = conditions.get(0);
        assertEquals("Warning does not match", null, condition.getWarningLevel());
        assertEquals("Error does not match", "1", condition.getErrorLevel());
        assertEquals("Actual does not match", "1", condition.getActualLevel());
        assertEquals("Name does not match", "new_security_rating", condition.getName());
        assertEquals("Status  does not match", ConditionStatus.OK, condition.getStatus());
    }

    @Test
    public void parseResponseAllPropertiesTest() throws Exception {
//        String input = "[{\"id\":7560,\"key\":\"com.test.service:my-service\",\"name\":\"Service Name\",\"scope\":\"PRJ\",\"qualifier\":\"TRK\",\"date\":\"2015-12-10T00:52:31+0000\",\"creationDate\":\"2015-12-02T20:00:16+0000\",\"lname\":\"Service Name\",\"version\":\"1.2.54\",\"description\":\"Service Name\",\"msr\":[{\"key\":\"quality_gate_details\",\"data\":\"{\\\"level\\\":\\\"ERROR\\\",\\\"conditions\\\":[{\\\"metric\\\":\\\"coverage\\\",\\\"op\\\":\\\"LT\\\",\\\"warning\\\":\\\"25\\\",\\\"error\\\":\\\"15\\\",\\\"actual\\\":\\\"11.3\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"critical_violations\\\",\\\"op\\\":\\\"NE\\\",\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"0\\\",\\\"actual\\\":\\\"25\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"blocker_violations\\\",\\\"op\\\":\\\"NE\\\",\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"0\\\",\\\"actual\\\":\\\"0\\\",\\\"level\\\":\\\"OK\\\"}]}\"}]}]";
        String input = "{\"component\":{\"id\":\"AV1ryMPb5ewUHfWU_dSL\",\"key\":\"com.stuq.demo:skeleton-service\",\"name\":\"Skeleton Service\",\"qualifier\":\"TRK\",\"measures\":[{\"metric\":\"quality_gate_details\",\"value\":\"{\\\"level\\\":\\\"ERROR\\\",\\\"conditions\\\":[{\\\"metric\\\":\\\"new_security_rating\\\",\\\"op\\\":\\\"GT\\\",\\\"period\\\":1,\\\"error\\\":\\\"1\\\",\\\"actual\\\":\\\"1\\\",\\\"level\\\":\\\"OK\\\"},{\\\"metric\\\":\\\"new_reliability_rating\\\",\\\"op\\\":\\\"GT\\\",\\\"period\\\":1,\\\"error\\\":\\\"1\\\",\\\"actual\\\":\\\"4\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"new_maintainability_rating\\\",\\\"op\\\":\\\"GT\\\",\\\"period\\\":1,\\\"error\\\":\\\"1\\\",\\\"actual\\\":\\\"3\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"new_coverage\\\",\\\"op\\\":\\\"LT\\\",\\\"period\\\":1,\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"80\\\",\\\"actual\\\":\\\"13.461538461538462\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"code_smells\\\",\\\"op\\\":\\\"GT\\\",\\\"warning\\\":\\\"5\\\",\\\"error\\\":\\\"20\\\",\\\"actual\\\":\\\"84\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"vulnerabilities\\\",\\\"op\\\":\\\"GT\\\",\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"0\\\",\\\"actual\\\":\\\"5\\\",\\\"level\\\":\\\"ERROR\\\"},{\\\"metric\\\":\\\"skipped_tests\\\",\\\"op\\\":\\\"GT\\\",\\\"warning\\\":\\\"5\\\",\\\"error\\\":\\\"10\\\",\\\"actual\\\":\\\"0\\\",\\\"level\\\":\\\"OK\\\"},{\\\"metric\\\":\\\"coverage\\\",\\\"op\\\":\\\"LT\\\",\\\"warning\\\":\\\"\\\",\\\"error\\\":\\\"80\\\",\\\"actual\\\":\\\"31.7\\\",\\\"level\\\":\\\"ERROR\\\"}]}\"}]}}";

        Result result = QueryExecutor.parseResponse(input);
        assertEquals("Id does not match", "AV1ryMPb5ewUHfWU_dSL", result.getId());
        assertEquals("Key does not match", "com.stuq.demo:skeleton-service", result.getKey());
        assertEquals("Name does not match", "Skeleton Service", result.getName());
//        assertEquals("Version does not match", "1.2.54", result.getVersion());

//        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
//        DateTime expectedDateTime = formatter.parseDateTime("2015-12-10T00:52:31+0000");
//        assertEquals("DateTime", expectedDateTime, result.getDatetime());
    }

    @Test(expected = SonarBreakException.class)
    public void parseResponseNonJSONTest() throws Exception {
        String input = "<html><body>ERROR</body></html>";
        QueryExecutor.parseResponse(input);
    }
}