package entityClasses;

public class Post {
    private int id;
    private String author;
    private String content;
    private String authorRole;

    public Post(int id, String author, String content, String authorRole) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.authorRole = authorRole;
    }

    public int getPostID() {  
        return this.id;
    }
    
    public String getAuthor() {  
        return this.author;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public String getAuthorRole() {
    	return authorRole;
    }

    // Setters
    public void setPostId(int id) {
        this.id = id;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }

    @Override
    public String toString() {
        return "id: " + id + " author: " + author + " content: " + content;
    }
}