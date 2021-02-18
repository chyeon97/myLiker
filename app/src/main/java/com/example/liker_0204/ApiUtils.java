package com.example.liker_0204;


public class ApiUtils {

    private static final String BASE_URL="http://teamb-iot.calit2.net/";

    public static UserService getUserService() {
        return ApiClient.getClient(BASE_URL).create(UserService.class);
    }

}
