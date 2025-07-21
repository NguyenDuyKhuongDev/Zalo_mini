package Helpers;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class SharedCurrentAccountManager {
    private static final String PREF_NAME = "current_account_pref";
    private static final String PHONE_LIST_KEY = "phone_list";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedCurrentAccountManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public List<String> loadCurrentPhoneNumbers() {
        Set<String> listPhoneNumber = sharedPreferences.getStringSet(PHONE_LIST_KEY, new HashSet<>());
        return new ArrayList<>(listPhoneNumber);
    }

    public void addPhoneNumber(String phoneNumber) {
        Set<String> listPhoneNumber = new HashSet<>(sharedPreferences.getStringSet(PHONE_LIST_KEY, new HashSet<>()));
        if (listPhoneNumber.contains(phoneNumber)) return;
        listPhoneNumber.add(phoneNumber);
        editor.putStringSet(PHONE_LIST_KEY, listPhoneNumber).apply();
    }

    public void removePhoneNumber(String phoneNumber) {
        Set<String> listPhoneNumber = new HashSet<>(sharedPreferences.getStringSet(PHONE_LIST_KEY, new HashSet<>()));
        if (listPhoneNumber.remove(phoneNumber)) {
            editor.putStringSet(PHONE_LIST_KEY, listPhoneNumber).apply();
        }
    }

    public void clearPhoneNumbers() {
        editor.remove(PHONE_LIST_KEY).apply();
    }

    public boolean isPhoneNumberExist(String phoneNumber) {
        Set<String> listPhoneNumber = sharedPreferences.getStringSet(PHONE_LIST_KEY, new HashSet<>());
        return listPhoneNumber.contains(phoneNumber);
    }

}
