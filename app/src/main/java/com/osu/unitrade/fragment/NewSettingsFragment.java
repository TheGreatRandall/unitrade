package com.osu.unitrade.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.osu.unitrade.R;
import com.osu.unitrade.activity.HomeActivity;
import com.osu.unitrade.activity.MainActivity;
import com.osu.unitrade.model.User;

public class NewSettingsFragment extends PreferenceFragmentCompat {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        Preference nickname = (Preference) getPreferenceManager().findPreference(getString(R.string.key_username));
        Preference email = (Preference) getPreferenceManager().findPreference(getString(R.string.key_email));

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    nickname.setSummary(userProfile.nickname);
                    email.setSummary(userProfile.emailAddress);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), getString(R.string.fail_get_user), Toast.LENGTH_SHORT).show();
            }
        });


        Preference button = (Preference) getPreferenceManager().findPreference("logoutButton");
        if (button != null) {
            button.setOnPreferenceClickListener(preference -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                Toast.makeText(getActivity(), getString(R.string.user_logged_out), Toast.LENGTH_LONG).show();
                return true;
            });
        }
    }


}