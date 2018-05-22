//package ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins;
//
//import ca.bc.gov.nrs.infra.cmdb.domain.services.InfrastructureRegistrationService;
//import ca.bc.gov.nrs.infra.cmdb.features.jenkins.UpsertBuilds;
//import ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins.AddBuildModel;
//import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.is;
//import static org.hamcrest.Matchers.not;
//import static org.hamcrest.Matchers.notNullValue;
//import static org.mockito.Mockito.mock;
//
//
//public class UpsertBuilds_Handler_MakeProjectMapTests
//{
//    public UpsertBuilds_Handler_MakeProjectMapTests()
//    {
//        this.sut = new UpsertBuilds.Handler(Mockito.mock(CmdbContext.class), Mockito.mock(InfrastructureRegistrationService.class));
//    }
//
//    private UpsertBuilds.Handler sut;
//
//    @Test
//    public void shouldReturnEmptyMap_whenListIsEmpty()
//    {
//        List<AddBuildModel> models = new ArrayList<>();
//
//        Map<String, Set<String>> result = this.sut.makeProjectMap(models);
//
//        MatcherAssert.assertThat(result, Matchers.is(Matchers.notNullValue()));
//    }
//
//    @Test
//    public void shouldReturnEmptyMap_whenListIsNull()
//    {
//        Map<String, Set<String>> result = this.sut.makeProjectMap(null);
//
//        MatcherAssert.assertThat(result, Matchers.is(Matchers.notNullValue()));
//    }
//
//    @Test
//    public void shouldCreateExpectedMap_whenListIsValid()
//    {
//        AddBuildModel a1 = new AddBuildModel();
//        a1.setProjectKey("A");
//        a1.setComponentName("a-1-api");
//        AddBuildModel a2 = new AddBuildModel();
//        a2.setProjectKey("A");
//        a2.setComponentName("a-2-api");
//        AddBuildModel a3 = new AddBuildModel();
//        a3.setProjectKey("A");
//        a3.setComponentName("a-3-api");
//        AddBuildModel a4 = new AddBuildModel();
//        a4.setProjectKey("A");
//        a4.setComponentName("a-1-api");
//        AddBuildModel b1 = new AddBuildModel();
//        b1.setProjectKey("B");
//        b1.setComponentName("b-1-api");
//        List<AddBuildModel> builds = Arrays.asList(a1, a2, a3, a4, b1);
//
//        Map<String,Set<String>> result = this.sut.makeProjectMap(builds);
//
//        MatcherAssert.assertThat("An unexpected number of projects have been accounted for", result.keySet().size(), Matchers.is(2));
//        MatcherAssert.assertThat(result.get("A").size(), Matchers.is(3));
//        MatcherAssert.assertThat(result.get("B").size(), Matchers.is(1));
//    }
//}
