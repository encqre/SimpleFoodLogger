package lt.jasinevicius.simplefoodlogger;

public class LoggerSettings {

    /********************Shared preferences keys and default values***************************/

    //General preferences
    public static final String PREFERENCE_THEME = "pref_theme";
    public static final String PREFERENCE_THEME_DEFAULT = "Light theme";
    public static final String PREFERENCE_UNITS = "pref_units";
    public static final String PREFERENCE_UNITS_DEFAULT = "Metric";
    public static final String PREFERENCE_TARGET_CALORIES = "pref_calories";
    public static final String PREFERENCE_TARGET_CALORIES_DEFAULT = "2500";
    public static final String PREFERENCE_TARGET_CALORIES_CALCULATED = "pref_calories_calculated";
    public static final String PREFERENCE_TARGET_PROTEIN_PERCENT = "pref_protein";
    public static final String PREFERENCE_TARGET_PROTEIN_PERCENT_DEFAULT = "25";
    public static final String PREFERENCE_TARGET_CARBS_PERCENT = "pref_carbs";
    public static final String PREFERENCE_TARGET_CARBS_PERCENT_DEFAULT = "45";
    public static final String PREFERENCE_TARGET_FAT_PERCENT = "pref_fat";
    public static final String PREFERENCE_TARGET_FAT_PERCENT_DEFAULT = "30";
    public static final String PREFERENCE_RECENT_FOODS_SIZE = "pref_recent_foods_size";
    public static final String PREFERENCE_RECENT_FOODS_SIZE_DEFAULT = "10";
    public static final String PREFERENCE_STATS_IGNORE_ZERO_KCAL_DAYS= "pref_stats_ignore_zero_kcal_days";

    //User profile preferences
    public static final String PREFERENCE_GENDER = "pref_gender";
    public static final String PREFERENCE_GENDER_DEFAULT = "Male";
    public static final String PREFERENCE_WEIGHT = "pref_weight";
    public static final String PREFERENCE_WEIGHT_DEFAULT = "80";
    public static final String PREFERENCE_HEIGHT = "pref_height";
    public static final String PREFERENCE_HEIGHT_DEFAULT = "175";
    public static final String PREFERENCE_AGE = "pref_age";
    public static final String PREFERENCE_AGE_DEFAULT = "25";
    public static final String PREFERENCE_ACTIVITY_LEVEL = "pref_activity_level";
    public static final String PREFERENCE_ACTIVITY_LEVEL_DEFAULT = "0";
    public static final String PREFERENCE_GOAL = "pref_goal";
    public static final String PREFERENCE_GOAL_DEFAULT = "0";

    //Tutorial/Initial setup related preferences
    public static final String PREFERENCE_INITIAL_DB_SETUP_NEEDED = "initial_database_setup_needed";
    public static final String PREFERENCE_INITIAL_PROFILE_SETUP_NEEDED = "initial_profile_setup_needed";
    public static final String PREFERENCE_TUTORIAL_HOME_PAGE_DONE = "tutorial_home_page_done";
    public static final String PREFERENCE_TUTORIAL_FOOD_LIST_DONE = "tutorial_food_list_done";
    public static final String PREFERENCE_TUTORIAL_STATISTICS_DONE = "tutorial_statistics_done";
    public static final String PREFERENCE_TUTORIAL_EDIT_FOOD_NONCUSTOM_DONE = "tutorial_edit_food_noncustom_done";

    //SettingsFragment specific
    public static final String PREFERENCE_BACKUP_LOGS = "pref_backup_logs";
    public static final String PREFERENCE_BACKUP_LOGS_LAST_DATE = "pref_last_backup_logs_date";
    public static final String PREFERENCE_BACKUP_CUSTOM_FOODS = "pref_backup_custom_foods";
    public static final String PREFERENCE_BACKUP_CUSTOM_FOODS_LAST_DATE = "pref_last_backup_custom_foods_date";
    public static final String PREFERENCE_IMPORT_LOGS = "pref_import_logs";
    public static final String PREFERENCE_IMPORT_CUSTOM_FOODS = "pref_import_custom_foods";
    public static final String PREFERENCE_SET_MACROS = "pref_macros";
    public static final String PREFERENCE_MANAGE_HIDDEN_FOODS = "pref_hidden_food_list";

}
