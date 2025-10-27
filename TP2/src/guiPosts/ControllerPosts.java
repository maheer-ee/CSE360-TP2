package guiPosts;

import java.sql.SQLException;
import java.util.List;
import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: ControllerPosts Class </p>
 * 
 * <p> Description: Controller for regular user posts view. This version includes permission
 * checks - users can only edit/delete their OWN content, unlike admins who can edit/delete
 * anything.</p>
 * 
 * <p>Key Security Features:
 * - Ownership verification before edit/delete operations
 * - User-friendly error dialogs for permission denials
 * - Role tracking on all new content
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Emmanuel Zelaya-Armenta, Lynn Robert Carter
 * @version 2.00 2025-10-26 Added permission checks and role tracking
 */
public class ControllerPosts {
    
    // ===================== POSTS PANEL METHODS ===================
    
    /*******
     * <p> Method: performViewPosts </p>
     * 
     * <p> Description: Retrieves and displays all posts. Regular users see all posts but
     * can only edit/delete their own.</p>
     */
    protected static void performViewPosts() {
        try {    
            // Get all posts from database
            List<Post> allPosts = ViewPosts.theDatabase.getAllPosts();
            
            // Clear existing items in ListView
            ViewPosts.list_Posts.getItems().clear();
            
            // Format and add each post to the display
            for (Post post : allPosts) { 
                // Format includes role badge [Admin], [Role1], or [Role2]
                String displayText = ModelPosts.formatPostForDisplay(post);
                ViewPosts.list_Posts.getItems().add(displayText);
            }
            
            // Switch to posts panel view
            ViewPosts.showPostsPanel();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 
    protected static void performCreatePost() {
        // Clear text area for new post
        ViewPosts.text_PostContent.setText("");
        
        // Show create post panel
        ViewPosts.showCreatePostsPanel();
    }
    
    /*******
     * <p> Method: performBack </p>
     * 
     * <p> Description: Returns user to their role-specific home page. Since this interface
     * can be accessed by Role1 or Role2 users, we check which role and go to the right home.</p>
     */
    protected static void performBack() {
    	// Check user's role and navigate to appropriate home page
    	if(ViewPosts.theUser.getAdminRole()) {
    		// Should not happen - admins use ViewPostsAdmin
    		guiAdminHome.ViewAdminHome.displayAdminHome(ViewPosts.theStage, ViewPosts.theUser);
    	}
    	else if(ViewPosts.theUser.getNewRole1()) {
    		// Role1 user - go to Role1 home
    		guiRole1.ViewRole1Home.displayRole1Home(ViewPosts.theStage, ViewPosts.theUser);
    	}
    	else {
    		// Role2 user - go to Role2 home
    		guiRole2.ViewRole2Home.displayRole2Home(ViewPosts.theStage, ViewPosts.theUser);
    	}
    }
    
    /*******
     * <p> Method: performDeletePost </p>
     * 
     * <p> Description: Deletes a post ONLY if the current user is the author.
     * This is the key permission check that prevents users from deleting others' posts.</p>
     * 
     * <p>Security Process:
     * 1. Get selected post ID
     * 2. Retrieve full Post object from database
     * 3. Compare post author with current user
     * 4. If match: allow delete
     * 5. If no match: show error dialog and deny
     * </p>
     */
    protected static void performDeletePost() {
        // Get selected post display string
        String p = ViewPosts.list_Posts.getSelectionModel().getSelectedItem();
        
        // Validate selection
        if(p == null) {
            System.out.println("Need to select a Post");
            return;
        }
        
        // Extract post ID from display string
        int id = ModelPosts.getID(p);
        
        try {
            // SECURITY CHECK: Get the full post object to check ownership
            Post post = ViewPosts.theDatabase.getPost(id);
            
            // Compare post author with current user's username
            if (!post.getAuthor().equals(ViewPosts.theUser.getUserName())) {
                // NOT the owner - deny access
                
                // Print to console for debugging
                System.out.println("ERROR: You can only delete your own posts!");
                
                // Show user-friendly error dialog
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Permission Denied");
                alert.setHeaderText("Cannot Delete Post");
                alert.setContentText("You can only delete your own posts.");
                
                // Show dialog and wait for user to click OK
                alert.showAndWait();
                
                // Exit method without deleting
                return;
            }
            
            // If we get here, user IS the owner - allow delete
            ViewPosts.theDatabase.deletePost(id);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Refresh the posts view
        performViewPosts();
    }
    
    /*******
     * <p> Method: performEditPost </p>
     * 
     * <p> Description: Edits a post ONLY if the current user is the author.
     * Similar permission check to performDeletePost.</p>
     * 
     * <p>Process:
     * 1. Verify ownership (security check)
     * 2. If authorized, show edit dialog
     * 3. Update database with new content
     * 4. Refresh view
     * </p>
     */
    protected static void performEditPost() {
        String p = ViewPosts.list_Posts.getSelectionModel().getSelectedItem();
        
        if(p == null || p.trim().isEmpty()) {
            System.out.println("Need to select a Post");
            return;
        }
        
        int id = ModelPosts.getID(p);
        
        try {
            // SECURITY CHECK: Verify ownership before allowing edit
            Post post = ViewPosts.theDatabase.getPost(id);
            
            // Compare authors
            if (!post.getAuthor().equals(ViewPosts.theUser.getUserName())) {
                // NOT the owner - deny access
                System.out.println("ERROR: You can only edit your own posts!");
                
                // Show error dialog
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Permission Denied");
                alert.setHeaderText("Cannot Edit Post");
                alert.setContentText("You can only edit your own posts.");
                alert.showAndWait();
                
                // Exit without editing
                return;
            }
            
            // Owner verified - proceed with edit
            
        } catch (SQLException e) {
            e.printStackTrace();
            return;  // Exit on database error
        }
       
        // Show edit dialog
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Edit Post");
        dialog.setHeaderText("Edit the post content:");
        dialog.setContentText("New content:");
        
        java.util.Optional<String> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            String newText = result.get().trim();
            
            if (newText.isEmpty()) {
                System.out.println("New content cannot be empty"); 
                return;
            }
            
            try {
                // Update post with new content
                ViewPosts.theDatabase.updatePost(id, newText);
                performViewPosts();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // ===================== CREATE POSTS PANEL METHODS ===================
    
    /*******
     * <p> Method: performSubmitPost </p>
     * 
     * <p> Description: Creates a new post with role tracking. Determines the user's role
     * and tags the post appropriately.</p>
     * 
     * <p>Role Detection:
     * - Checks Role1 first (if statement)
     * - Then checks Role2 (else if)
     * - Defaults to "Unknown" if neither (shouldn't happen)
     * </p>
     * 
     * <p>Note: Admin check not needed since admins use the ViewPostsAdmin interface.</p>
     */
    protected static void performSubmitPost() {
        String a = ViewPosts.theUser.getUserName();
        String c = ViewPosts.text_PostContent.getText();
        
        // Determine user's role for tagging the post
        String role = "Unknown";  // Default fallback
        
        if (ViewPosts.theUser.getAdminRole()) {
            role = "Admin";
        }
        else if (ViewPosts.theUser.getNewRole1()) {
            role = "Role1";
        }
        else if (ViewPosts.theUser.getNewRole2()) {
            role = "Role2";
        }
        
        if(c.equals("")) {
            System.out.println("Post Cannot be empty");
            return;
        }
        
        try {
            ViewPosts.theDatabase.createPost(a, c, role);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        ControllerPosts.performViewPosts();
    }
    protected static void performCancel() {
        // Discard typed content and return to posts view
        ViewPosts.showPostsPanel();
    }
    
    // ===================== REPLIES PANEL METHODS ===================
    
    protected static void performCreateReply() {
        // Clear reply text area
        ViewPosts.text_ReplyContent.setText("");
        
        // Show create reply panel
        ViewPosts.showCreateReplyPanel();
    }
    
    /*******
     * <p> Method: performViewReplies </p>
     * 
     * <p> Description: Shows all replies for the selected post. Creates a thread view.</p>
     */
    protected static void performViewReplies() {
        String p = ViewPosts.list_Posts.getSelectionModel().getSelectedItem();
        
        if(p == null || p.trim().isEmpty()) {
            System.out.println("Need to select a Post");
            return;
        }
        
        int id = ModelPosts.getID(p);
        
        try {
            // Get and display the original post
            Post post = ViewPosts.theDatabase.getPost(id);
            ViewPosts.text_PostInReply.setText(post.getContent());
           
            // Get all replies for this post
            List<Reply> postReplies = ViewPosts.theDatabase.getRepliesByPost(id);
            
            // Clear existing replies from ListView
            ViewPosts.list_Replies.getItems().clear();
            
            // Format and display each reply
            for (Reply reply : postReplies) {
                String displayText = ModelPosts.formatReplyForDisplay(reply);
                ViewPosts.list_Replies.getItems().add(displayText);
            }
            
            // Store current post ID for reply creation
            ViewPosts.currentPostID = id;
            
            // Show replies panel
            ViewPosts.showRepliesPanel();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /*******
     * <p> Method: performDeleteReply </p>
     * 
     * <p> Description: Deletes a reply ONLY if the current user is the author.
     * Same permission model as posts.</p>
     */
    protected static void performDeleteReply() {
        String r = ViewPosts.list_Replies.getSelectionModel().getSelectedItem();
        
        if(r == null || r.trim().isEmpty()) {
            System.out.println("Need to select a Reply");
            return;
        }
        
        int id = ModelPosts.getID(r);
        
        try {
            // SECURITY CHECK: Verify ownership
            Reply reply = ViewPosts.theDatabase.getReply(id);
            
            // Compare authors
            if (!reply.getAuthor().equals(ViewPosts.theUser.getUserName())) {
                // NOT the owner - deny
                System.out.println("ERROR: You can only delete your own replies!");
                
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Permission Denied");
                alert.setHeaderText("Cannot Delete Reply");
                alert.setContentText("You can only delete your own replies.");
                alert.showAndWait();
                
                return;
            }
            
            // Owner verified - allow delete
            ViewPosts.theDatabase.deleteReply(id);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Refresh replies view
        performViewReplies();
    }
    
    /*******
     * <p> Method: performEditReply </p>
     * 
     * <p> Description: Edits a reply ONLY if the current user is the author.
     * Same permission model as posts.</p>
     */
    protected static void performEditReply() {
        String r = ViewPosts.list_Replies.getSelectionModel().getSelectedItem();
        
        if(r == null || r.trim().isEmpty()) {
            System.out.println("Need to select a Reply");
            return;
        }
       
        int id = ModelPosts.getID(r);
        
        try {
            // SECURITY CHECK: Verify ownership
            Reply reply = ViewPosts.theDatabase.getReply(id);
            
            // Compare authors
            if (!reply.getAuthor().equals(ViewPosts.theUser.getUserName())) {
                // NOT the owner - deny
                System.out.println("ERROR: You can only edit your own replies!");
                
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Permission Denied");
                alert.setHeaderText("Cannot Edit Reply");
                alert.setContentText("You can only edit your own replies.");
                alert.showAndWait();
                
                return;
            }
            
            // Owner verified - proceed with edit
            
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        
        // Show edit dialog
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Edit Reply");
        dialog.setHeaderText("Edit the reply content:");
        dialog.setContentText("New content:");
        
        java.util.Optional<String> newContent = dialog.showAndWait();
        
        if (newContent.isPresent()) {
            String newText = newContent.get().trim();
            
            if (newText.isEmpty()) {
                System.out.println("New content cannot be empty");
                return;
            }
            
            try {
                // Update reply with new content
                ViewPosts.theDatabase.updateReply(id, newText);
                performViewReplies();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    protected static void performBackToPosts() {
        // Return to posts list view
        ViewPosts.showPostsPanel();
    }
    
    // ===================== CREATE REPLIES PANEL METHODS ===================
    
    /*******
     * <p> Method: performSubmitReply </p>
     * 
     * <p> Description: Creates a new reply with role tracking. Determines user's role
     * and tags the reply appropriately.</p>
     */
    protected static void performSubmitReply() {
        // Get author (current user)
        String a = ViewPosts.theUser.getUserName();
        
        // Get reply content
        String c = ViewPosts.text_ReplyContent.getText();
        
        // Determine user's role
        String role = "Unknown";
        if (ViewPosts.theUser.getNewRole1()) {
            role = "Role1";
        }
        else if (ViewPosts.theUser.getNewRole2()) {
            role = "Role2";
        }
        
        // Validate not empty
        if(c.equals("")) {
            System.out.println("Reply Cannot be empty again");
            return;
        }
        
        try {
            // Create reply with role tag
            // Uses currentPostID to link reply to post
            ViewPosts.theDatabase.createReply(ViewPosts.currentPostID, a, c, role);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Return to replies view
        ControllerPosts.performViewReplies();
    }    
    
    protected static void performReplyCancel() {
        // Discard and return to replies view
        ControllerPosts.performViewReplies();
    }
    
    // ===================== NAVIGATION METHODS ===================
    
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewPosts.theStage);
    }
    
    protected static void performQuit() {
        System.exit(0);
    }
}