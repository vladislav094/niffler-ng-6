package guru.qa.niffler.api;

import guru.qa.niffler.model.TokenResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface AuthApi {

    @GET("register")
    Call<Void> getRegisterForm();

    @POST("register")
    @FormUrlEncoded
    Call<Void> registerUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordSubmit,
            @Field("_csrf") String csrf
    );

    @GET("oauth2/authorize")
    Call<Void> getOauth2Authorize(
            @Query("response_type") String responseType,
            @Query("client_id") String clientId,
            @Query("scope") String scope,
            @Query("redirect_uri") String redirectUri,
            @Query("code_challenge") String codeChallenge,
            @Query("code_challenge_method") String codeChallengeMethod
    );

    @POST("login")
    @FormUrlEncoded
    Call<Void> login(
            @Field("_csrf") String csrf,
            @Field("username") String username,
            @Field("password") String password
    );

    @POST("oauth2/token")
    @FormUrlEncoded
    Call<TokenResponse> token(
            @Field("code") String code,
            @Field(value = "redirect_uri", encoded = true) String redirectUri,
            @Field("code_verifier") String codeVerifier,
            @Field("grant_type") String grantType,
            @Field("client_id") String client
    );

    @POST("oauth2/token")
    @FormUrlEncoded
    Call<Void> tokenFieldMap(@FieldMap Map<String, String> fieldMap);
}
