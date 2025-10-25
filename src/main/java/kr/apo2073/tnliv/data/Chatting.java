package kr.apo2073.tnliv.data;

public class Chatting {
    private final String id;
    private final String nickName;
    private final String comment;

    public Chatting(String id, String nickName, String comment) {
        this.id = id;
        this.nickName = nickName;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public String getComment() {
        if (comment==null) return "";
        return comment;
    }

    @Override
    public String toString() {
        return "Chatting{" +
                "id='" + id + '\'' +
                ", nickName='" + nickName + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chatting chatting = (Chatting) o;

        if (!id.equals(chatting.id)) return false;
        if (!nickName.equals(chatting.nickName)) return false;
        return comment.equals(chatting.comment);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + nickName.hashCode();
        result = 31 * result + comment.hashCode();
        return result;
    }
}

