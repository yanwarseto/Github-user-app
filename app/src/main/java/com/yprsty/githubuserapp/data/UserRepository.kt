package com.yprsty.githubuserapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.yprsty.githubuserapp.data.local.entity.UsersEntity
import com.yprsty.githubuserapp.data.local.room.UsersDao
import com.yprsty.githubuserapp.data.remote.responseGson.ResponseUser
import com.yprsty.githubuserapp.data.remote.responseGson.ResponseUserDetail
import com.yprsty.githubuserapp.data.remote.responseGson.ResponseUsersWithUsername
import com.yprsty.githubuserapp.data.remote.retrofit.ApiService
import com.yprsty.githubuserapp.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(
    private val apiService: ApiService,
    private val usersDao: UsersDao,
    private val appExecutors: AppExecutors
) {

    private val result = MediatorLiveData<Result<List<UsersEntity>>>()
    private val userDetail = MediatorLiveData<Result<ResponseUserDetail>>()
    private val followers = MediatorLiveData<Result<List<UsersEntity>>>()
    private val following = MediatorLiveData<Result<List<UsersEntity>>>()

    /**
     * LOCAL SOURCE
     * Get all user favorites
     *
     * @sample UsersDao
     * */
    fun getFavoriteUsers(): LiveData<List<UsersEntity>> = usersDao.getFavoriteUsers()

    /**
     * LOCAL SOURCE
     * Add user to database and set is_favorite is true
     *
     * @sample UsersDao
     * */
    suspend fun setBookmarkedUsers(users: UsersEntity, isFavorite: Boolean) {
        users.isFavorite = isFavorite
        if (isFavorite) {
            usersDao.insert(users)
        } else {
            usersDao.delete(users)
        }
    }

    /**
     * NETWORK SOURCE
     * Searching user with username
     *
     * search/users?q={username}
     *
     * @param username
     * @sample ApiService.getListUsers
     * @sample ResponseUsersWithUsername
     * */
    fun getAllUserWithUsername(username: String): LiveData<Result<List<UsersEntity>>> {
        result.value = Result.Loading

        val client = apiService.getListUsers(username)
        client.enqueue(object : Callback<ResponseUsersWithUsername> {
            override fun onResponse(
                call: Call<ResponseUsersWithUsername>,
                response: Response<ResponseUsersWithUsername>
            ) {
                if (response.isSuccessful) {
                    val allUsers = response.body()?.items
                    val usersList = ArrayList<UsersEntity>()
                    appExecutors.diskIO.execute {
                        allUsers?.forEach { user ->
                            val isFavorite = usersDao.isUserFavorite(user.login) // checking data
                            val userEntity = UsersEntity(
                                user.login,
                                user.avatarUrl,
                                isFavorite
                            ) // changing data for adapter
                            usersList.add(userEntity)
                        }
                        result.postValue(Result.Success(usersList))
                    }
                }
            }

            override fun onFailure(call: Call<ResponseUsersWithUsername>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
        return result
    }

    /**
     * NETWORK SOURCE
     * Searching s specific user with username
     *
     * users/{username}
     *
     * @param username
     * @sample ApiService.getUser
     * @sample ResponseUserDetail
     * */
    fun getUserWithUsername(username: String): LiveData<Result<ResponseUserDetail>> {
        userDetail.value = Result.Loading
        val client = apiService.getUser(username)
        client.enqueue(object : Callback<ResponseUserDetail> {
            override fun onResponse(
                call: Call<ResponseUserDetail>,
                response: Response<ResponseUserDetail>
            ) {
                if (response.isSuccessful) {
                    val responseUserDetail = response.body() as ResponseUserDetail
                    userDetail.value = Result.Success(responseUserDetail)

                    // get list follower and following user
                    getUserFollowers(username)
                    getUserFollowing(username)
                }
            }

            override fun onFailure(call: Call<ResponseUserDetail>, t: Throwable) {
                userDetail.value = Result.Error(t.message.toString())
            }
        })
        return userDetail
    }

    // for ViewModel access
    fun getFollowers() = followers

    // for ViewModel access
    fun getFollowing() = following

    /**
     * NETWORK SOURCE
     * Get all followers from a user
     *
     * users/{username}/followers
     *
     * @param username
     * @sample ApiService.getUserFollowers
     * @sample ResponseUser
     * */
    private fun getUserFollowers(username: String) {
        followers.value = Result.Loading
        val client = apiService.getUserFollowers(username)
        client.enqueue(object : Callback<List<ResponseUser>> {
            override fun onResponse(
                call: Call<List<ResponseUser>>,
                response: Response<List<ResponseUser>>
            ) {
                if (response.isSuccessful) {
                    val allFollowers = response.body()
                    val followerList = ArrayList<UsersEntity>()
                    appExecutors.diskIO.execute {
                        allFollowers?.forEach { user ->
                            val isFavorite = usersDao.isUserFavorite(user.login)
                            val userEntity = UsersEntity(user.login, user.avatarUrl, isFavorite)
                            followerList.add(userEntity)
                        }
                        followers.postValue(Result.Success(followerList))
                    }
                }
            }

            override fun onFailure(call: Call<List<ResponseUser>>, t: Throwable) {
                followers.value = Result.Error(t.message.toString())
            }
        })
    }

    /**
     * NETWORK SOURCE
     * Get all following from a user
     *
     * users/{username}/following
     *
     * @param username
     * @sample ApiService.getUserFollowing
     * @sample ResponseUser
     * */
    private fun getUserFollowing(username: String) {
        following.value = Result.Loading
        val client = apiService.getUserFollowing(username)
        client.enqueue(object : Callback<List<ResponseUser>> {
            override fun onResponse(
                call: Call<List<ResponseUser>>,
                response: Response<List<ResponseUser>>
            ) {
                if (response.isSuccessful) {
                    val allFollowing = response.body()
                    val followingList = ArrayList<UsersEntity>()
                    appExecutors.diskIO.execute {
                        allFollowing?.forEach { user ->
                            val isFavorite = usersDao.isUserFavorite(user.login)
                            val userEntity = UsersEntity(user.login, user.avatarUrl, isFavorite)
                            followingList.add(userEntity)
                        }
                        following.postValue(Result.Success(followingList))
                    }
                }
            }

            override fun onFailure(call: Call<List<ResponseUser>>, t: Throwable) {
                following.value = Result.Error(t.message.toString())
            }
        })
    }

    // Singleton initiate
    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            usersDao: UsersDao,
            appExecutors: AppExecutors
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, usersDao, appExecutors)
            }.also { instance = it }
    }
}