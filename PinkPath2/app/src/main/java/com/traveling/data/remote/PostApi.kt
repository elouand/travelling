package com.traveling.data.remote

import com.google.gson.annotations.SerializedName
import com.traveling.domain.model.Post
import com.traveling.domain.model.UserData
import com.traveling.NetworkConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

data class CommentRequest(val text: String, val authorId: Int)
data class LikeRequest(val userId: Int)
data class LikeResponse(
    val likes: Int,
    val isLiked: Boolean? = null
)

data class CommentResponse(
    val id: Int,
    val text: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("authorName") val authorName: String? = null,
    @SerializedName("authorAvatarUrl") val authorAvatarUrl: String? = null
)

interface PostApi {
    @GET("photos")
    suspend fun getPosts(@Query("userId") userId: Int? = null): List<Post>

    @Multipart
    @POST("photos")
    suspend fun uploadPhoto(
        @Part image: MultipartBody.Part,
        @Part audio: MultipartBody.Part?,
        @Part("description") description: RequestBody,
        @Part("type_lieu") type_lieu: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("is_public") is_public: RequestBody,
        @Part("authorId") authorId: RequestBody?
    ): Post

    @POST("photos/{photoId}/like")
    suspend fun likePost(
        @Path("photoId") photoId: String,
        @Body request: LikeRequest
    ): LikeResponse

    @GET("photos/{photoId}/comments")
    suspend fun getComments(@Path("photoId") photoId: String): List<CommentResponse>

    @POST("photos/{photoId}/comments")
    suspend fun addComment(
        @Path("photoId") photoId: String,
        @Body request: CommentRequest
    ): CommentResponse
}
