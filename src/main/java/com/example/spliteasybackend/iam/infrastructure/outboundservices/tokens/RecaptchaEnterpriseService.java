package com.example.spliteasybackend.iam.infrastructure.outboundservices.tokens;

import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceClient;
import com.google.recaptchaenterprise.v1.Assessment;
import com.google.recaptchaenterprise.v1.CreateAssessmentRequest;
import com.google.recaptchaenterprise.v1.Event;
import com.google.recaptchaenterprise.v1.TokenProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RecaptchaEnterpriseService {

    @Value("${recaptcha.enterprise.project-id}")
    private String projectId;

    @Value("${recaptcha.android.site-key}")
    private String androidSiteKey;

    public Result verifyAndroid(String token, String expectedAction, String expectedPackage, double minScore) {
        try (RecaptchaEnterpriseServiceClient client = RecaptchaEnterpriseServiceClient.create()) {
            String parent = "projects/" + projectId;

            Event event = Event.newBuilder()
                    .setToken(token)
                    .setSiteKey(androidSiteKey)
                    .build();

            Assessment req = Assessment.newBuilder()
                    .setEvent(event)
                    .build();

            CreateAssessmentRequest car = CreateAssessmentRequest.newBuilder()
                    .setParent(parent)
                    .setAssessment(req)
                    .build();

            Assessment res = client.createAssessment(car);
            TokenProperties props = res.getTokenProperties();

            if (!props.getValid()) {
                return Result.fail("invalid_reason=" + props.getInvalidReason().name());
            }
            if (expectedAction != null && !expectedAction.isBlank()
                    && !props.getAction().equalsIgnoreCase(expectedAction)) {
                return Result.fail("action_mismatch expected=" + expectedAction + " got=" + props.getAction());
            }
            if (expectedPackage != null && !expectedPackage.isBlank()) {
                String pkg = props.getAndroidPackageName();
                if (!expectedPackage.equals(pkg)) {
                    return Result.fail("package_mismatch expected=" + expectedPackage + " got=" + pkg);
                }
            }

            float score = res.getRiskAnalysis().getScore();
            if (score < (float) minScore) {
                return Result.fail("low_score=" + score);
            }
            return Result.ok(score);
        } catch (Exception e) {
            return Result.fail("exception=" + e.getClass().getSimpleName() + ":" + e.getMessage());
        }
    }

    public record Result(boolean ok, String reason, float score) {
        public static Result ok(float s) { return new Result(true,  "", s); }
        public static Result fail(String r){ return new Result(false, r, -1f); }
    }
}
