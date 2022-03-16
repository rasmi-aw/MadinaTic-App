package com.techload.madinatic.data.networking;

public interface Requests {

    //
    String SERVER_PREFIX = "https://madina-tic.herokuapp.com";
    String CHECK_DATABASE_TABLES = SERVER_PREFIX + "/admin/stats";
    String AUTHENTICATION = SERVER_PREFIX + "/auth/login";
    String SIGN_UP = SERVER_PREFIX + "/auth/signup";
    String DELETE_ACCOUNT = SERVER_PREFIX + "/user/disable-acount";
    String NEWS = SERVER_PREFIX + "/actus";
    String CHECK_USER_TYPE = SERVER_PREFIX + "/user/is-auth";
    String EDIT_ACCOUNT = SERVER_PREFIX + "/user/infos";
    String RESET_PASSWORD_EMAIL = SERVER_PREFIX + "/auth/reset-password";
    String GET_ALL_REPORTS = SERVER_PREFIX + "/signalement/all";
    String GET_USER_INFO = SERVER_PREFIX + "/user/infos";
    String GET_USER_REPORTS = SERVER_PREFIX + "/signalement";
    String GET_CATEGORIES = SERVER_PREFIX + "/signalement/get-all-categories";
    String CHANGE_USER_PASSWORD = SERVER_PREFIX + "/auth/change_password";
    String DELETE_REPORT = SERVER_PREFIX + "/signalement/delete/id";
    String ADD_NEW_REPORT = SERVER_PREFIX + "/signalement/add";
    String EDIT_OR_COMPLETE_REPORT = SERVER_PREFIX + "/signalement/edit/id";
    String IMAGES = SERVER_PREFIX + "/";
    String VIDEOS = SERVER_PREFIX + "/";

}
