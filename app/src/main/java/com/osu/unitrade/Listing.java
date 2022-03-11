package com.osu.unitrade;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Listing {

    public String uid;
    public String title;
    public String description;

    public Listing() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Listing(String uid, String title, String description) {
        this.uid = uid;
        this.title = title;
        this.description = description;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title", title);
        result.put("description", description);
        return result;
    }
}