package com.projemanag.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val fcm_token_updated: String="fcmTokenUpdated"
    const val fcm_token : String ="fcmToken"

    // Firebase Constants
    // This  is used for the collection name for USERS.
    const val USERS: String = "users"

    // This  is used for the collection name for USERS.
    const val BOARDS: String = "boards"

    // Firebase database field names
    const val ID : String ="id"
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val ASSIGNED_TO: String = "assignedTo"
    const val DOCUMENT_ID: String = "documentId"
    const val TASK_LIST: String = "taskList"
    const val board_detail ="board_detail"
    //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult
    const val READ_STORAGE_PERMISSION_CODE = 1
    // A unique code of image selection from Phone Storage.
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val EMAIL : String = "email"

    const val TASKLIST_POSITION : String="tasklist_position"
    const val CARDLIST_POSITION : String ="cardlist_position"
    const val board_members_list : String = "board_members_list"
    const val select : String ="Select"
    const val unselect : String ="UnSelect"
    const val project_preferences : String  ="trello_prefs"

    //FCM
    const val fcm_serverkey : String ="AAAAB_n5RT0:APA91bFY4GZ6Hu4fu8r4MrKM3iP2PNJMHg0AxSMUKeiyFUzDsXVHng8bFjdyW1K0b1tg9wLwzkSowVEssUn_5tbeQxYm8Vh1ELBxy-VXJoodYXdGBt2CDz97ys1oc0VTzuL6vDC0ZLCc"
    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"
    /**
     * A function for user profile image selection from phone storage.
     */
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    /**
     * A function to get the extension of selected image.
     */
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}