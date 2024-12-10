package guru.qa.niffler.api;

import guru.qa.niffler.model.UdUserJson;
import guru.qa.niffler.model.rest.pageable.RestResponsePage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import javax.annotation.Nullable;

public interface GatewayV2Api {

    @GET("api/v2/friends/all")
    Call<RestResponsePage<UdUserJson>> allFriends(@Header("Authorization") String bearerToken,
                                                  @Query("searchQuery") @Nullable String searchQuery,
                                                  @Query("page") int page,
                                                  @Query("sort") String sort);

    @GET("api/v2/users/all")
    Call<RestResponsePage<UdUserJson>> allUsers(@Header("Authorization") String bearerToken,
                                                  @Query("searchQuery") @Nullable String searchQuery,
                                                  @Query("page") int page,
                                                  @Query("sort") String sort);
}
