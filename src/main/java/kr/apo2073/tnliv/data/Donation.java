package kr.apo2073.tnliv.data;

import java.util.Objects;

public class Donation {
    private final String alertBox;
    private final String id;
    private final String nickName;
    private final String comment;
    private final long amount;

    public Donation(String id, String nickName, String comment, long amount, String alertBox) {
        this.id = id;
        this.nickName = nickName;
        this.comment = comment;
        this.amount = amount;
        this.alertBox=alertBox;
    }

    public String getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public String getComment() {
        if (comment==null) return "";
        if (comment.startsWith("video://") || comment.startsWith("[yt:")) return "영상후원";
        return comment;
    }

    public long getAmount() {
        return amount;
    }

    public String getAlertBox() {return alertBox;}

    @Override
    public String toString() {
        return "Donation{" +
                "id='" + id + '\'' +
                ", nickName='" + nickName + '\'' +
                ", comment='" + comment + '\'' +
                ", amount=" + amount +
                ", key=" + alertBox +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donation donation = (Donation) o;
        return amount == donation.amount &&
                Objects.equals(id, donation.id) &&
                Objects.equals(nickName, donation.nickName) &&
                Objects.equals(comment, donation.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nickName, comment, amount);
    }
}
