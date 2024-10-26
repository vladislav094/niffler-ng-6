package guru.qa.niffler.api;

import guru.qa.niffler.model.UdUserJson;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;
import java.util.List;

public interface UserdataApi {

    @GET("internal/users/current")
    Call<UdUserJson> getCurrentUser(@Query("username") String username);

    @GET("internal/users/all")
    Call<List<UdUserJson>> getAllUsers(@Query("username") String username,
                                       @Query("searchQuery") @Nullable String searchQuery);

    @POST("internal/users/update")
    Call<UdUserJson> updateUser(@Body UdUserJson user);

    @POST("internal/invitations/send")
    Call<UdUserJson> sendInvitation(@Query("username") String username,
                                    @Query("targetUsername") String targetUsername);

    @POST("internal/invitations/accept")
    Call<UdUserJson> acceptInvitation(@Query("username") String username,
                                    @Query("targetUsername") String targetUsername);

    @POST("internal/invitations/decline")
    Call<UdUserJson> declineInvitation(@Query("username") String username,
                                      @Query("targetUsername") String targetUsername);

    @GET("internal/friends/all")
    Call<List<UdUserJson>> getAllFriends(@Query("username") String username,
                                       @Query("searchQuery") @Nullable String searchQuery);

    @DELETE("internal/friends/remove")
    Call<Void> removeFriend(@Query("username") String username,
                            @Query("targetUsername") String targetUsername);
}
