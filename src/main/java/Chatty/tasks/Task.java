package Chatty.tasks;

public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void mark(){
        this.isDone = true;
    }

    public void unmark(){
        this.isDone = false;
    }
    public String getDescription() {
        return description;
    }
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }
    public void setIsDone(boolean isDone){
        this.isDone = isDone;
    }
    public boolean getIsDone(){
        return isDone;
    }
    public String saveFormat() {
        return "";
    }
    public boolean containsKeyword(String keyword) {
        return description.toLowerCase().contains(keyword.toLowerCase());
    }

}
