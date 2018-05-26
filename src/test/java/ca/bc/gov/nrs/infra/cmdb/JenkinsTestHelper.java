package ca.bc.gov.nrs.infra.cmdb;

import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Project;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsPromotion;
import ca.bc.gov.nrs.infra.cmdb.features.jenkins.BuildImportDeserializer;
import ca.bc.gov.nrs.infra.cmdb.features.jenkins.Import;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class JenkinsTestHelper
{
    private final Logger log = LoggerFactory.getLogger(JenkinsTestHelper.class);

    JenkinsTestHelper()
    {
        components.put("AQUA", Arrays.asList("aqua-permit-api", "aqua-as-cfg", "aqua-permit-wso2"));
        components.put("NPE", Arrays.asList("npe-phonebook-api", "npe-client-war"));
    }

    @Getter
    @Setter
    private String user = "user";

    @Getter
    @Setter
    private String result = "SUCCESS";

    @Getter
    @Setter
    private String environment = "0_INTEGRATION";

    public JenkinsPromotion makeRandomPromotion()
    {
        JenkinsBuild build = makeRandomBuild();
        return makeRandomPromotionOfBuild(build);
    }

    public JenkinsPromotion makeRandomPromotionOfBuild(JenkinsBuild build)
    {
        int number = new Random().nextInt(100);
        String url = new StringBuilder().append("https://myapps.com/int/jenkins/job/")
                .append(build.getComponent().getProject().getKey())
                .append("/job/")
                .append(build.getComponent().getName())
                .append("/")
                .append(build.getNumber())
                .append("/promotion/")
                .append(this.environment)
                .append("/promotionBuild/")
                .append(number)
                .toString();

        JenkinsPromotion promotion = JenkinsPromotion.of(build)
                .environment(this.environment)
                .number(number)
                .url(url)
                .startedAt(System.currentTimeMillis())
                .took(1235L)
                .result(this.result)
                .triggeredByUsername(this.user)
                .build();

        log.debug("Generated random promotion: {}", promotion.toString());

        return promotion;
    }


    public JenkinsBuild makeRandomBuild()
    {
        Component component = makeRandomComponent();
        return makeRandomBuildOfComponent(component);
    }

    public JenkinsBuild makeRandomBuildOfComponent(Component component)
    {
        int number = new Random().nextInt(200);
        String url = new StringBuilder().append("https://myapps.com/int/jenkins/job/")
                .append(component.getProject().getKey())
                .append("/job/")
                .append(component.getName())
                .append("/")
                .append(number)
                .toString();

        JenkinsBuild build = JenkinsBuild.of(component)
                .number(number)
                .url(url)
                .startedAt(System.currentTimeMillis())
                .took(4156L)
                .result(this.result)
                .triggeredByUsername(this.user)
                .build();

        log.debug("Generated random build: {}", build.toString());

        return build;
    }

    public Component makeRandomComponent()
    {
        Object[] keys = this.components.keySet().toArray();
        Object projectKey = keys[new Random().nextInt(keys.length)];

        Object[] values = this.components.get(keys.toString()).toArray();
        Object componentName = values[new Random().nextInt(values.length)];

        Project project = Project.withKey(projectKey.toString()).build();
        Component component = Component.ofName(componentName.toString())
                .belongsTo(project)
                .build();

        log.debug("Generated random component: {}", component.toString());

        return component;
    }


    private Map<String,List<String>> components = new HashMap<>();

    public Import.Command readBuildsJson() throws IOException
    {
        String content = new String(Files.readAllBytes(Paths.get("src/test/resources/jenkins/builds.json")));
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Import.Command.class, new BuildImportDeserializer());
        mapper.registerModule(module);

        return mapper.readValue(content, Import.Command.class);
    }

}
