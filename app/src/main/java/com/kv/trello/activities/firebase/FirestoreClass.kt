package com.kv.trello.activities.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kv.trello.activities.*
import com.kv.trello.activities.model.Board
import com.kv.trello.activities.model.Card
import com.kv.trello.activities.model.User
import com.projemanag.utils.Constants

/**
 * A custom class where we will add the operation performed for the firestore database.
 */
class FirestoreClass {

    // Create a instance of Firebase Firestore
    private val mFireStore = FirebaseFirestore.getInstance()
    fun registerUser(activity: SignUpActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(getCurrentUserID())
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    /**
     * A function to SignIn using firebase and get the user details from Firestore Database.
     */
    fun loadUserData(activity: Activity, readBoardsList: Boolean = false) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!

                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }

    /**
     * A function to update the user profile data into the database.
     */
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS) // Collection Name
            .document(getCurrentUserID()) // Document ID
            .update(userHashMap) // A hashmap of fields which are to be updated.
            .addOnSuccessListener {
                // Profile data is updated successfully.
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")

                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                // Notify the success result.
                when(activity){
                    is MyProfileActivity ->{
                        activity.profileUpdateSuccess()
                    }
                    is MainActivity ->{
                        activity.tokenupdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity){
                    is MyProfileActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

    /**
     * A function for creating a board and making an entry in the database.
     */

    fun createBoard(activity: CreateBoardActivity, board: Board) {

        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board created successfully.")

                Toast.makeText(activity, "Board created successfully.", Toast.LENGTH_SHORT).show()

                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

    /**
     * A function to get the list of created boards from the database.
     */
    fun getBoardsList(activity: MainActivity) {

        // The collection name for BOARDS
        mFireStore.collection(Constants.BOARDS)
            // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // Here we have created a new instance for Boards ArrayList.
                val boardsList: ArrayList<Board> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id

                    boardsList.add(board)
                }

                // Here pass the result to the base activity.
                activity.populateBoardsListToUI(boardsList)
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    /**
     * A function to get the Board Details.
     */
    fun getBoardDetails(activity: TaskListActivity, documentId: String) {
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id

                // Send the result of board to the base activity.
                activity.boardDetails(board)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    /**
     * A function to create a task list in the board detail.
     */
    fun addUpdateTaskList(activity: Activity, board: Board) {

        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully.")
                if(activity is TaskListActivity)
                    activity.addUpdateTaskListSuccess()
                else if (activity is CardDetailsActivity)
                    activity.addUpdateTaskListSuccess()
            }
            .addOnFailureListener { e ->
                if(activity is TaskListActivity)
                    activity.hideProgressDialog()
                else if (activity is CardDetailsActivity)
                    activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    fun getmemebersList(board : Board,activity : Activity){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID,board.assignedTo)
            .get()
            .addOnSuccessListener {
                it->
                Log.e(activity.javaClass.simpleName,it.documents.toString())
                val users : ArrayList<User> =ArrayList()
                for( i in it.documents){
                    val user = i.toObject(User::class.java)!!
                    users.add(user)
                }
                if(activity is MembersActivity)
                    activity.setupMembersList(users)
                else if(activity is TaskListActivity)
                    activity.boardMembers(users)

            }.addOnFailureListener { e ->
                if(activity is MembersActivity)
                    activity.hideProgressDialog()
                else if(activity is TaskListActivity)
                    activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting members.", e)
            }
    }

    /**
     * A function for getting the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getMemberDetails(activity : MembersActivity,email : String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener {
                it->
                if(it.documents.size>0){
                    val user= it.documents[0].toObject(User::class.java)
                    activity.memberDetails(user!!)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No member found with this email")
                }
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting member.", e)
            }
    }
    fun assignMembersToBoard(activity :MembersActivity,user:User,board: Board,remove : Boolean){
        val assignedHashMap= HashMap<String,Any>()
        assignedHashMap[Constants.ASSIGNED_TO]=board.assignedTo
        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user,remove)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while adding members.", e)
            }
    }
    fun removeBoard(activity : MainActivity,boards : ArrayList<Board>,id : String ){

        mFireStore.collection(Constants.BOARDS)
            .document(id)
            .delete()
            .addOnSuccessListener {
                activity.removeBoardSuccess()
            }
            .addOnFailureListener {
                Toast.makeText(activity,"Failed..",Toast.LENGTH_SHORT).show()
            }

    }
}