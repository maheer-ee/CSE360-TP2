package guiPosts;

import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: ModelPosts Class </p>
 * 
 * <p> Description: Model class for regular user posts view. Provides utility methods
 * for formatting posts and replies with role badges.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Emmanuel Zelaya-Armenta, Lynn Robert Carter
 * @version 2.00 2025-10-26 Added role badge display
 */
public class ModelPosts {
	
	/*******
	 * <p> Method: getID </p>
	 * 
	 * <p> Description: Extracts the ID number from a formatted post/reply string.
	 * See detailed explanation in ModelPostsAdmin.</p>
	 * 
	 * @param s formatted display string
	 * @return extracted ID number
	 */
    protected static int getID(String s) {
        // Parse "id: X author: Y..." to extract X
        String[] stuff1 = s.split(" author: ");
        String temp = stuff1[0];
        String[] stuff2 = temp.split(" ");
        int id = Integer.parseInt(stuff2[1]);
        return id;
    }
    
    /*******
     * <p> Method: formatPostForDisplay </p>
     * 
     * <p> Description: Formats a Post object for display with role badge.</p>
     * 
     * <p>Format: "id: X author: username [Role] content: text"</p>
     * 
     * @param post the Post to format
     * @return formatted display string
     */
    protected static String formatPostForDisplay(Post post) {
        // Get role, defaulting to "Unknown" if null
        String role = post.getAuthorRole() != null ? post.getAuthorRole() : "Unknown";
        
        // Build formatted string with role badge
        return "id: " + post.getPostID() + 
               " author: " + post.getAuthor() + 
               " [" + role + "] " +
               "content: " + post.getContent();
    }
    
    /*******
     * <p> Method: formatReplyForDisplay </p>
     * 
     * <p> Description: Formats a Reply object for display with role badge.</p>
     * 
     * @param reply the Reply to format
     * @return formatted display string
     */
    protected static String formatReplyForDisplay(Reply reply) {
        // Get role, defaulting to "Unknown" if null
        String role = reply.getAuthorRole() != null ? reply.getAuthorRole() : "Unknown";
        
        // Build formatted string with role badge
        return "id: " + reply.getReplyID() + 
               " author: " +reply.getAuthor() + 
               " [" + role + "] " +
               "content: " + reply.getContent();
    }
}