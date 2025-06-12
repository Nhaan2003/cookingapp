//package com.example.cookingtutorialapp.database;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//    private static final String DATABASE_NAME = "cooking_tutorial.db";
//    private static final int DATABASE_VERSION = 1;
//
//    // Table names
//    public static final String TABLE_USERS = "users";
//    public static final String TABLE_CATEGORIES = "categories";
//    public static final String TABLE_RECIPES = "recipes";
//    public static final String TABLE_STEPS = "steps";
//    public static final String TABLE_INGREDIENTS = "ingredients";
//    public static final String TABLE_FAVORITES = "favorites";
//    public static final String TABLE_SHOPPING_LIST = "shopping_list";
//
//    // Common column names
//    public static final String COLUMN_ID = "id";
//
//    // Users columns
//    public static final String COLUMN_USERNAME = "username";
//    public static final String COLUMN_EMAIL = "email";
//    public static final String COLUMN_PASSWORD = "password";
//
//    // Categories columns
//    public static final String COLUMN_CATEGORY_NAME = "category_name";
//
//    // Recipes columns
//    public static final String COLUMN_RECIPE_NAME = "recipe_name";
//    public static final String COLUMN_RECIPE_DESCRIPTION = "description";
//    public static final String COLUMN_RECIPE_IMAGE = "image";
//    public static final String COLUMN_CATEGORY_ID = "category_id";
//    public static final String COLUMN_USER_ID = "user_id";
//    public static final String COLUMN_RECIPE_COOKING_TIME = "cooking_time";
//
//    // Steps columns
//    public static final String COLUMN_RECIPE_ID = "recipe_id";
//    public static final String COLUMN_STEP_NUMBER = "step_number";
//    public static final String COLUMN_STEP_DESCRIPTION = "step_description";
//
//    // Ingredients columns
//    public static final String COLUMN_INGREDIENT_NAME = "ingredient_name";
//    public static final String COLUMN_INGREDIENT_QUANTITY = "quantity";
//    public static final String COLUMN_INGREDIENT_UNIT = "unit";
//
//    // Favorites columns
//    // Uses COLUMN_USER_ID and COLUMN_RECIPE_ID
//
//    // Shopping list columns
//    public static final String COLUMN_INGREDIENT_ID = "ingredient_id";
//    public static final String COLUMN_IS_PURCHASED = "is_purchased";
//
//    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        // Create users table
//        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COLUMN_USERNAME + " TEXT,"
//                + COLUMN_EMAIL + " TEXT UNIQUE,"
//                + COLUMN_PASSWORD + " TEXT" + ")";
//
//        // Create categories table
//        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COLUMN_CATEGORY_NAME + " TEXT" + ")";
//
//        // Create recipes table
//        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + "("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COLUMN_RECIPE_NAME + " TEXT,"
//                + COLUMN_RECIPE_DESCRIPTION + " TEXT,"
//                + COLUMN_RECIPE_IMAGE + " TEXT,"
//                + COLUMN_CATEGORY_ID + " INTEGER,"
//                + COLUMN_USER_ID + " INTEGER,"
//                + COLUMN_RECIPE_COOKING_TIME + " TEXT,"
//                + "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_ID + "),"
//                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")" + ")";
//
//        // Create steps table
//        String CREATE_STEPS_TABLE = "CREATE TABLE " + TABLE_STEPS + "("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COLUMN_RECIPE_ID + " INTEGER,"
//                + COLUMN_STEP_NUMBER + " INTEGER,"
//                + COLUMN_STEP_DESCRIPTION + " TEXT,"
//                + "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_ID + ")" + ")";
//
//        // Create ingredients table
//        String CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_INGREDIENTS + "("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COLUMN_RECIPE_ID + " INTEGER,"
//                + COLUMN_INGREDIENT_NAME + " TEXT,"
//                + COLUMN_INGREDIENT_QUANTITY + " REAL,"
//                + COLUMN_INGREDIENT_UNIT + " TEXT,"
//                + "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_ID + ")" + ")";
//
//        // Create favorites table
//        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COLUMN_USER_ID + " INTEGER,"
//                + COLUMN_RECIPE_ID + " INTEGER,"
//                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "),"
//                + "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_ID + ")" + ")";
//
//        // Create shopping list table
//        String CREATE_SHOPPING_LIST_TABLE = "CREATE TABLE " + TABLE_SHOPPING_LIST + "("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COLUMN_USER_ID + " INTEGER,"
//                + COLUMN_INGREDIENT_ID + " INTEGER,"
//                + COLUMN_IS_PURCHASED + " INTEGER DEFAULT 0,"
//                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "),"
//                + "FOREIGN KEY(" + COLUMN_INGREDIENT_ID + ") REFERENCES " + TABLE_INGREDIENTS + "(" + COLUMN_ID + ")" + ")";
//
//        db.execSQL(CREATE_USERS_TABLE);
//        db.execSQL(CREATE_CATEGORIES_TABLE);
//        db.execSQL(CREATE_RECIPES_TABLE);
//        db.execSQL(CREATE_STEPS_TABLE);
//        db.execSQL(CREATE_INGREDIENTS_TABLE);
//        db.execSQL(CREATE_FAVORITES_TABLE);
//        db.execSQL(CREATE_SHOPPING_LIST_TABLE);
//
//        // Insert default categories
//        insertDefaultCategories(db);
//    }
//
//    private void insertDefaultCategories(SQLiteDatabase db) {
//        String[] categories = {"Món chính", "Món phụ", "Món tráng miệng", "Đồ uống", "Món chay"};
//
//        for (String category : categories) {
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_CATEGORY_NAME, category);
//            db.insert(TABLE_CATEGORIES, null, values);
//        }
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
//        onCreate(db);
//    }
//}

package com.example.cookingtutorialapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cooking_tutorial.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_STEPS = "steps";
    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String TABLE_FAVORITES = "favorites";
    public static final String TABLE_SHOPPING_LIST = "shopping_list";

    // Common column names
    public static final String COLUMN_ID = "id";

    // Users columns
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    // Categories columns
    public static final String COLUMN_CATEGORY_NAME = "category_name";

    // Recipes columns
    public static final String COLUMN_RECIPE_NAME = "recipe_name";
    public static final String COLUMN_RECIPE_DESCRIPTION = "description";
    public static final String COLUMN_RECIPE_IMAGE = "image";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_RECIPE_COOKING_TIME = "cooking_time";

    // Steps columns
    public static final String COLUMN_RECIPE_ID = "recipe_id";
    public static final String COLUMN_STEP_NUMBER = "step_number";
    public static final String COLUMN_STEP_DESCRIPTION = "step_description";

    // Ingredients columns
    public static final String COLUMN_INGREDIENT_NAME = "ingredient_name";
    public static final String COLUMN_INGREDIENT_QUANTITY = "quantity";
    public static final String COLUMN_INGREDIENT_UNIT = "unit";

    // Favorites columns
    // Uses COLUMN_USER_ID and COLUMN_RECIPE_ID

    // Shopping list columns
    public static final String COLUMN_INGREDIENT_ID = "ingredient_id";
    public static final String COLUMN_IS_PURCHASED = "is_purchased";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT" + ")";

        // Create categories table
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CATEGORY_NAME + " TEXT" + ")";

        // Create recipes table
        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_RECIPE_NAME + " TEXT,"
                + COLUMN_RECIPE_DESCRIPTION + " TEXT,"
                + COLUMN_RECIPE_IMAGE + " TEXT,"
                + COLUMN_CATEGORY_ID + " INTEGER,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_RECIPE_COOKING_TIME + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")" + ")";

        // Create steps table
        String CREATE_STEPS_TABLE = "CREATE TABLE " + TABLE_STEPS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_RECIPE_ID + " INTEGER,"
                + COLUMN_STEP_NUMBER + " INTEGER,"
                + COLUMN_STEP_DESCRIPTION + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_ID + ")" + ")";

        // Create ingredients table
        String CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_INGREDIENTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_RECIPE_ID + " INTEGER,"
                + COLUMN_INGREDIENT_NAME + " TEXT,"
                + COLUMN_INGREDIENT_QUANTITY + " REAL,"
                + COLUMN_INGREDIENT_UNIT + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_ID + ")" + ")";

        // Create favorites table
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_RECIPE_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_ID + ")" + ")";

        // Create shopping list table
        String CREATE_SHOPPING_LIST_TABLE = "CREATE TABLE " + TABLE_SHOPPING_LIST + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_INGREDIENT_ID + " INTEGER,"
                + COLUMN_IS_PURCHASED + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_INGREDIENT_ID + ") REFERENCES " + TABLE_INGREDIENTS + "(" + COLUMN_ID + ")" + ")";

        // Tạo các bảng
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_STEPS_TABLE);
        db.execSQL(CREATE_INGREDIENTS_TABLE);
        db.execSQL(CREATE_FAVORITES_TABLE);
        db.execSQL(CREATE_SHOPPING_LIST_TABLE);

        // Thêm dữ liệu mẫu
        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            // 1. Thêm người dùng mẫu
            long adminId = insertUser(db, "admin", "admin@example.com", "admin123");

            // 2. Thêm các danh mục
            long monChinhId = insertCategory(db, "Món chính");
            long monPhuId = insertCategory(db, "Món phụ");
            long trangMiengId = insertCategory(db, "Món tráng miệng");
            long doUongId = insertCategory(db, "Đồ uống");
            long monChayId = insertCategory(db, "Món chay");
            long banhMyId = insertCategory(db, "Bánh mỳ & Bánh ngọt");
            long monAnSangId = insertCategory(db, "Món ăn sáng");

            // 3. Thêm công thức và chi tiết

            // PHỞ BÒ
            long phoBoId = insertRecipe(db, "Phở bò Hà Nội",
                    "Phở bò truyền thống của Hà Nội với nước dùng đậm đà từ xương bò và gia vị",
                    "pho_bo", monChinhId, adminId, "2 giờ 30 phút");

            // Các bước thực hiện
            insertStep(db, phoBoId, 1, "Rửa sạch xương bò, cho vào nồi nước lạnh và đun sôi trong 5 phút.");
            insertStep(db, phoBoId, 2, "Đổ bỏ nước, rửa lại xương dưới vòi nước để loại bỏ bọt và chất bẩn.");
            insertStep(db, phoBoId, 3, "Cho xương vào nồi nước mới, thêm hành tây, gừng nướng, hoa hồi, quế, đinh hương.");
            insertStep(db, phoBoId, 4, "Đun nhỏ lửa trong 2 giờ để lấy nước dùng.");
            insertStep(db, phoBoId, 5, "Thái thịt bò thành những lát mỏng.");
            insertStep(db, phoBoId, 6, "Trụng bánh phở trong nước sôi khoảng 10-15 giây.");
            insertStep(db, phoBoId, 7, "Xếp bánh phở vào tô, thêm hành xanh, thịt bò tái.");
            insertStep(db, phoBoId, 8, "Chan nước dùng nóng vào, thêm rau thơm, giá đỗ và ăn kèm với tương ớt, chanh.");

            // Nguyên liệu
            insertIngredient(db, phoBoId, "Xương bò", 1.5, "kg");
            insertIngredient(db, phoBoId, "Thịt bò phi lê", 300.0, "g");
            insertIngredient(db, phoBoId, "Bánh phở", 500.0, "g");
            insertIngredient(db, phoBoId, "Hành tây", 2.0, "củ");
            insertIngredient(db, phoBoId, "Gừng", 50.0, "g");
            insertIngredient(db, phoBoId, "Hoa hồi", 3.0, "cái");
            insertIngredient(db, phoBoId, "Quế", 2.0, "thanh");
            insertIngredient(db, phoBoId, "Đinh hương", 5.0, "cái");
            insertIngredient(db, phoBoId, "Hành lá", 50.0, "g");
            insertIngredient(db, phoBoId, "Rau thơm các loại", 100.0, "g");
            insertIngredient(db, phoBoId, "Giá đỗ", 100.0, "g");
            insertIngredient(db, phoBoId, "Chanh", 2.0, "quả");
            insertIngredient(db, phoBoId, "Ớt tươi", 3.0, "quả");

            // BÚN CHẢ
            long bunChaId = insertRecipe(db, "Bún chả",
                    "Bún chả thơm ngon với thịt nướng béo ngậy kèm nước chấm chua ngọt đặc trưng",
                    "bun_cha", monChinhId, adminId, "1 giờ");

            // Các bước thực hiện
            insertStep(db, bunChaId, 1, "Thịt ba chỉ thái miếng vừa ăn, thịt vai xay nhỏ và vo thành viên.");
            insertStep(db, bunChaId, 2, "Ướp thịt với hỗn hợp: hành tím băm, tỏi băm, nước mắm, đường, hạt tiêu, dầu ăn. Ướp 30 phút.");
            insertStep(db, bunChaId, 3, "Nướng thịt trên bếp than hoa hoặc lò nướng đến khi thịt chín vàng.");
            insertStep(db, bunChaId, 4, "Pha nước chấm: nước lọc, đường, giấm, nước mắm, tỏi băm, ớt băm.");
            insertStep(db, bunChaId, 5, "Ngâm củ cải trắng và cà rốt thái sợi vào nước chấm khoảng 15 phút.");
            insertStep(db, bunChaId, 6, "Trụng bún trong nước sôi, để ráo.");
            insertStep(db, bunChaId, 7, "Phục vụ bún với thịt nướng, nước chấm và rau sống các loại.");

            // Nguyên liệu
            insertIngredient(db, bunChaId, "Thịt ba chỉ", 300.0, "g");
            insertIngredient(db, bunChaId, "Thịt vai", 200.0, "g");
            insertIngredient(db, bunChaId, "Bún", 500.0, "g");
            insertIngredient(db, bunChaId, "Hành tím", 30.0, "g");
            insertIngredient(db, bunChaId, "Tỏi", 20.0, "g");
            insertIngredient(db, bunChaId, "Nước mắm", 4.0, "muỗng canh");
            insertIngredient(db, bunChaId, "Đường", 3.0, "muỗng canh");
            insertIngredient(db, bunChaId, "Giấm", 2.0, "muỗng canh");
            insertIngredient(db, bunChaId, "Củ cải trắng", 100.0, "g");
            insertIngredient(db, bunChaId, "Cà rốt", 100.0, "g");
            insertIngredient(db, bunChaId, "Ớt tươi", 3.0, "quả");
            insertIngredient(db, bunChaId, "Rau sống các loại", 200.0, "g");

            // GỎI CUỐN
            long goiCuonId = insertRecipe(db, "Gỏi cuốn tôm thịt",
                    "Gỏi cuốn với tôm, thịt heo, bún và rau thơm cuốn trong bánh tráng",
                    "goi_cuon", monPhuId, adminId, "30 phút");

            // Các bước thực hiện
            insertStep(db, goiCuonId, 1, "Luộc tôm với một ít muối đến khi chín, sau đó bóc vỏ và chẻ đôi theo chiều dọc.");
            insertStep(db, goiCuonId, 2, "Luộc thịt heo với gừng và hành tím đến khi chín, sau đó thái thành lát mỏng.");
            insertStep(db, goiCuonId, 3, "Ngâm bánh tráng trong nước ấm đến khi mềm.");
            insertStep(db, goiCuonId, 4, "Xếp lần lượt rau xà lách, húng quế, bún, thịt heo và tôm lên bánh tráng.");
            insertStep(db, goiCuonId, 5, "Cuộn chặt bánh tráng lại, bịt kín hai đầu.");
            insertStep(db, goiCuonId, 6, "Pha nước chấm từ tương đen, tương ớt, nước cốt chanh và lạc rang giã nhỏ.");
            insertStep(db, goiCuonId, 7, "Thưởng thức gỏi cuốn với nước chấm.");

            // Nguyên liệu
            insertIngredient(db, goiCuonId, "Tôm sú", 300.0, "g");
            insertIngredient(db, goiCuonId, "Thịt heo thăn", 250.0, "g");
            insertIngredient(db, goiCuonId, "Bánh tráng", 15.0, "cái");
            insertIngredient(db, goiCuonId, "Bún", 200.0, "g");
            insertIngredient(db, goiCuonId, "Xà lách", 100.0, "g");
            insertIngredient(db, goiCuonId, "Húng quế", 50.0, "g");
            insertIngredient(db, goiCuonId, "Tương đen", 3.0, "muỗng canh");
            insertIngredient(db, goiCuonId, "Tương ớt", 1.0, "muỗng canh");
            insertIngredient(db, goiCuonId, "Chanh", 2.0, "quả");
            insertIngredient(db, goiCuonId, "Lạc rang", 50.0, "g");
            insertIngredient(db, goiCuonId, "Gừng", 20.0, "g");
            insertIngredient(db, goiCuonId, "Hành tím", 2.0, "củ");

            // CHÈ SEN
            long cheSenId = insertRecipe(db, "Chè sen nhân hạt sen",
                    "Chè sen thanh mát với hạt sen bùi bùi và nước đường thơm nhẹ",
                    "che_sen", trangMiengId, adminId, "45 phút");

            // Các bước thực hiện
            insertStep(db, cheSenId, 1, "Ngâm hạt sen khô trong nước ấm khoảng 2 giờ.");
            insertStep(db, cheSenId, 2, "Luộc hạt sen với một chút muối đến khi hạt sen mềm.");
            insertStep(db, cheSenId, 3, "Nấu nước đường: cho đường, nước và lá dứa vào nồi đun sôi.");
            insertStep(db, cheSenId, 4, "Thêm hạt sen vào nồi nước đường, đun nhỏ lửa khoảng 15 phút.");
            insertStep(db, cheSenId, 5, "Pha bột năng với nước lạnh, từ từ đổ vào nồi chè để tạo độ sánh.");
            insertStep(db, cheSenId, 6, "Thêm lá dứa cắt nhỏ và vài giọt nước cốt dừa.");
            insertStep(db, cheSenId, 7, "Múc chè ra bát, rắc thêm mè trắng rang thơm.");

            // Nguyên liệu
            insertIngredient(db, cheSenId, "Hạt sen khô", 200.0, "g");
            insertIngredient(db, cheSenId, "Đường phèn", 150.0, "g");
            insertIngredient(db, cheSenId, "Lá dứa", 5.0, "lá");
            insertIngredient(db, cheSenId, "Bột năng", 20.0, "g");
            insertIngredient(db, cheSenId, "Nước cốt dừa", 100.0, "ml");
            insertIngredient(db, cheSenId, "Mè trắng", 10.0, "g");
            insertIngredient(db, cheSenId, "Muối", 0.5, "muỗng cà phê");

            // TRÀ ĐÀO CAM SẢ
            long traDaoId = insertRecipe(db, "Trà đào cam sả",
                    "Thức uống giải khát thanh mát với hương vị đào thơm ngọt kết hợp với cam và sả",
                    "tra_dao", doUongId, adminId, "30 phút");

            // Các bước thực hiện
            insertStep(db, traDaoId, 1, "Rửa sạch đào, bỏ hạt và cắt thành lát mỏng.");
            insertStep(db, traDaoId, 2, "Rửa sạch cam, vắt lấy nước cốt.");
            insertStep(db, traDaoId, 3, "Rửa sạch sả, đập dập và cắt khúc.");
            insertStep(db, traDaoId, 4, "Đun sôi nước, cho túi trà đen vào và ngâm khoảng 5 phút.");
            insertStep(db, traDaoId, 5, "Cho đường vào trà nóng, khuấy đều đến khi đường tan hết.");
            insertStep(db, traDaoId, 6, "Thêm sả vào trà đường và đợi nguội.");
            insertStep(db, traDaoId, 7, "Khi trà nguội, cho vào bình thủy tinh cùng với đá viên.");
            insertStep(db, traDaoId, 8, "Thêm lát đào và nước cốt cam, khuấy đều.");
            insertStep(db, traDaoId, 9, "Trang trí với lá bạc hà và lát đào.");

            // Nguyên liệu
            insertIngredient(db, traDaoId, "Đào tươi", 2.0, "quả");
            insertIngredient(db, traDaoId, "Cam", 1.0, "quả");
            insertIngredient(db, traDaoId, "Sả", 3.0, "cây");
            insertIngredient(db, traDaoId, "Túi trà đen", 2.0, "túi");
            insertIngredient(db, traDaoId, "Đường", 50.0, "g");
            insertIngredient(db, traDaoId, "Đá viên", 200.0, "g");
            insertIngredient(db, traDaoId, "Lá bạc hà", 5.0, "lá");

            // ĐẬU HŨ SỐT CÀ CHUA
            long dauHuId = insertRecipe(db, "Đậu hũ sốt cà chua",
                    "Món chay đơn giản nhưng đầy đủ dinh dưỡng với đậu hũ chiên giòn sốt cà chua chua ngọt",
                    "dau_hu_sot", monChayId, adminId, "25 phút");

            // Các bước thực hiện
            insertStep(db, dauHuId, 1, "Cắt đậu hũ thành những miếng vuông vừa ăn.");
            insertStep(db, dauHuId, 2, "Chiên đậu hũ trong dầu nóng đến khi vàng giòn đều các mặt.");
            insertStep(db, dauHuId, 3, "Vớt đậu hũ ra để ráo dầu.");
            insertStep(db, dauHuId, 4, "Phi thơm hành tỏi băm với dầu ăn.");
            insertStep(db, dauHuId, 5, "Cho cà chua cắt múi cau vào xào nhuyễn.");
            insertStep(db, dauHuId, 6, "Thêm nước, đường, muối, hạt nêm chay vào nồi, đun sôi.");
            insertStep(db, dauHuId, 7, "Cho đậu hũ vào sốt, đun nhỏ lửa 5 phút.");
            insertStep(db, dauHuId, 8, "Rắc hành lá, ngò rí cắt nhỏ lên trên và tắt bếp.");

            // Nguyên liệu
            insertIngredient(db, dauHuId, "Đậu hũ", 400.0, "g");
            insertIngredient(db, dauHuId, "Cà chua", 3.0, "quả");
            insertIngredient(db, dauHuId, "Hành tím", 2.0, "củ");
            insertIngredient(db, dauHuId, "Tỏi", 3.0, "tép");
            insertIngredient(db, dauHuId, "Đường", 1.0, "muỗng canh");
            insertIngredient(db, dauHuId, "Muối", 0.5, "muỗng cà phê");
            insertIngredient(db, dauHuId, "Hạt nêm chay", 1.0, "muỗng cà phê");
            insertIngredient(db, dauHuId, "Dầu ăn", 3.0, "muỗng canh");
            insertIngredient(db, dauHuId, "Hành lá", 2.0, "cây");
            insertIngredient(db, dauHuId, "Ngò rí", 10.0, "g");

            // BÁNH BÔNG LAN
            long banhBongLanId = insertRecipe(db, "Bánh bông lan truyền thống",
                    "Bánh bông lan mềm, xốp với hương vị thơm ngon của trứng và vani",
                    "banh_bong_lan", banhMyId, adminId, "50 phút");

            // Các bước thực hiện
            insertStep(db, banhBongLanId, 1, "Đánh trứng và đường cho đến khi hỗn hợp bông và chuyển sang màu trắng nhạt.");
            insertStep(db, banhBongLanId, 2, "Rây bột mì, bột baking powder vào hỗn hợp trứng.");
            insertStep(db, banhBongLanId, 3, "Trộn đều từ dưới lên trên để giữ độ bông của hỗn hợp.");
            insertStep(db, banhBongLanId, 4, "Cho bơ đã tan chảy và tinh chất vani vào, trộn nhẹ.");
            insertStep(db, banhBongLanId, 5, "Đổ bột vào khuôn đã lót giấy nến.");
            insertStep(db, banhBongLanId, 6, "Nướng ở nhiệt độ 180°C trong 25-30 phút.");
            insertStep(db, banhBongLanId, 7, "Kiểm tra độ chín bằng cách cắm tăm vào bánh, nếu tăm rút ra sạch là bánh đã chín.");
            insertStep(db, banhBongLanId, 8, "Để nguội hoàn toàn trước khi cắt bánh.");

            // Nguyên liệu
            insertIngredient(db, banhBongLanId, "Trứng gà", 5.0, "quả");
            insertIngredient(db, banhBongLanId, "Đường", 100.0, "g");
            insertIngredient(db, banhBongLanId, "Bột mì", 100.0, "g");
            insertIngredient(db, banhBongLanId, "Bột baking powder", 5.0, "g");
            insertIngredient(db, banhBongLanId, "Bơ", 50.0, "g");
            insertIngredient(db, banhBongLanId, "Tinh chất vani", 5.0, "ml");

            // BÁNH MÌ TRỨNG
            long banhMiTrungId = insertRecipe(db, "Bánh mì trứng",
                    "Bánh mì giòn thơm kèm trứng chiên vàng óng, ăn kèm với rau và nước sốt mayonnaise",
                    "banh_mi_trung", monAnSangId, adminId, "15 phút");

            // Các bước thực hiện
            insertStep(db, banhMiTrungId, 1, "Cắt bánh mì theo chiều dọc nhưng không đứt hẳn.");
            insertStep(db, banhMiTrungId, 2, "Đánh tan trứng với một chút muối và tiêu.");
            insertStep(db, banhMiTrungId, 3, "Chiên trứng trên chảo với dầu ăn đến khi vàng hai mặt.");
            insertStep(db, banhMiTrungId, 4, "Xếp rau xà lách, dưa leo vào trong bánh mì.");
            insertStep(db, banhMiTrungId, 5, "Đặt trứng chiên vào bánh mì.");
            insertStep(db, banhMiTrungId, 6, "Phết một lớp mayonnaise và tương ớt lên trên.");
            insertStep(db, banhMiTrungId, 7, "Rắc thêm hành lá và ngò rí cho thơm.");

            // Nguyên liệu
            insertIngredient(db, banhMiTrungId, "Bánh mì", 1.0, "ổ");
            insertIngredient(db, banhMiTrungId, "Trứng gà", 2.0, "quả");
            insertIngredient(db, banhMiTrungId, "Xà lách", 30.0, "g");
            insertIngredient(db, banhMiTrungId, "Dưa leo", 1.0, "quả");
            insertIngredient(db, banhMiTrungId, "Mayonnaise", 15.0, "g");
            insertIngredient(db, banhMiTrungId, "Tương ớt", 10.0, "g");
            insertIngredient(db, banhMiTrungId, "Hành lá", 5.0, "g");
            insertIngredient(db, banhMiTrungId, "Ngò rí", 5.0, "g");
            insertIngredient(db, banhMiTrungId, "Muối", 0.25, "muỗng cà phê");
            insertIngredient(db, banhMiTrungId, "Tiêu", 0.25, "muỗng cà phê");
            insertIngredient(db, banhMiTrungId, "Dầu ăn", 5.0, "ml");

            // SINH TỐ BƠ
            long sinhToBoId = insertRecipe(db, "Sinh tố bơ",
                    "Sinh tố bơ béo ngậy với sữa đặc và đá xay",
                    "sinh_to_bo", doUongId, adminId, "10 phút");

            // Các bước thực hiện
            insertStep(db, sinhToBoId, 1, "Bơ chín bóc vỏ, bỏ hạt, thái nhỏ cho vào máy xay sinh tố.");
            insertStep(db, sinhToBoId, 2, "Thêm sữa đặc, đá viên và sữa tươi vào máy xay.");
            insertStep(db, sinhToBoId, 3, "Xay nhuyễn cho đến khi hỗn hợp mịn.");
            insertStep(db, sinhToBoId, 4, "Đổ sinh tố ra ly, có thể thêm một ít đá bào lên trên.");
            insertStep(db, sinhToBoId, 5, "Trang trí với lá bạc hà và thưởng thức ngay.");

            // Nguyên liệu
            insertIngredient(db, sinhToBoId, "Bơ", 1.0, "quả");
            insertIngredient(db, sinhToBoId, "Sữa đặc", 2.0, "muỗng canh");
            insertIngredient(db, sinhToBoId, "Sữa tươi", 100.0, "ml");
            insertIngredient(db, sinhToBoId, "Đá viên", 10.0, "viên");
            insertIngredient(db, sinhToBoId, "Lá bạc hà", 1.0, "lá");

            // BÚN BÒ HUẾ
            long bunBoHueId = insertRecipe(db, "Bún bò Huế",
                    "Món bún bò cay nồng đặc trưng của xứ Huế với vị sả và mắm ruốc",
                    "bun_bo_hue", monChinhId, adminId, "2 giờ");

            // Các bước thực hiện
            insertStep(db, bunBoHueId, 1, "Xương bò và giò heo rửa sạch, cho vào nồi nước lạnh, đun sôi rồi hớt bọt.");
            insertStep(db, bunBoHueId, 2, "Cho sả đập dập, hành tím, gừng, ớt vào nồi nước dùng.");
            insertStep(db, bunBoHueId, 3, "Đun nhỏ lửa trong 1,5 giờ cho nước dùng ngọt.");
            insertStep(db, bunBoHueId, 4, "Phi thơm ớt bột, ớt sa tế với dầu ăn.");
            insertStep(db, bunBoHueId, 5, "Cho mắm ruốc vào phi cùng, sau đó cho vào nồi nước dùng.");
            insertStep(db, bunBoHueId, 6, "Nêm nếm lại với muối, đường, bột ngọt cho vừa ăn.");
            insertStep(db, bunBoHueId, 7, "Trụng bún, để ráo và cho vào tô.");
            insertStep(db, bunBoHueId, 8, "Thêm thịt bò thái lát, chả Huế, giò heo thái lát.");
            insertStep(db, bunBoHueId, 9, "Chan nước dùng nóng vào, thêm hành lá, rau thơm và ăn kèm với rau giá.");

            // Nguyên liệu
            insertIngredient(db, bunBoHueId, "Xương bò", 1.0, "kg");
            insertIngredient(db, bunBoHueId, "Giò heo", 500.0, "g");
            insertIngredient(db, bunBoHueId, "Thịt bò", 300.0, "g");
            insertIngredient(db, bunBoHueId, "Chả Huế", 200.0, "g");
            insertIngredient(db, bunBoHueId, "Bún", 500.0, "g");
            insertIngredient(db, bunBoHueId, "Sả", 5.0, "cây");
            insertIngredient(db, bunBoHueId, "Hành tím", 100.0, "g");
            insertIngredient(db, bunBoHueId, "Gừng", 50.0, "g");
            insertIngredient(db, bunBoHueId, "Ớt", 5.0, "quả");
            insertIngredient(db, bunBoHueId, "Mắm ruốc", 2.0, "muỗng canh");
            insertIngredient(db, bunBoHueId, "Ớt bột", 1.0, "muỗng canh");
            insertIngredient(db, bunBoHueId, "Ớt sa tế", 1.0, "muỗng canh");
            insertIngredient(db, bunBoHueId, "Rau giá, rau thơm", 200.0, "g");
            insertIngredient(db, bunBoHueId, "Hành lá", 30.0, "g");
            insertIngredient(db, bunBoHueId, "Muối", 1.0, "muỗng canh");
            insertIngredient(db, bunBoHueId, "Đường", 1.0, "muỗng canh");
            insertIngredient(db, bunBoHueId, "Bột ngọt", 0.5, "muỗng cà phê");

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private long insertUser(SQLiteDatabase db, String username, String email, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    private long insertCategory(SQLiteDatabase db, String categoryName) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, categoryName);
        return db.insert(TABLE_CATEGORIES, null, values);
    }

    private long insertRecipe(SQLiteDatabase db, String recipeName, String description, String image,
                              long categoryId, long userId, String cookingTime) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPE_NAME, recipeName);
        values.put(COLUMN_RECIPE_DESCRIPTION, description);
        values.put(COLUMN_RECIPE_IMAGE, image);
        values.put(COLUMN_CATEGORY_ID, categoryId);
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_RECIPE_COOKING_TIME, cookingTime);
        return db.insert(TABLE_RECIPES, null, values);
    }

    private void insertStep(SQLiteDatabase db, long recipeId, int stepNumber, String description) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPE_ID, recipeId);
        values.put(COLUMN_STEP_NUMBER, stepNumber);
        values.put(COLUMN_STEP_DESCRIPTION, description);
        db.insert(TABLE_STEPS, null, values);
    }

    private long insertIngredient(SQLiteDatabase db, long recipeId, String name, double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPE_ID, recipeId);
        values.put(COLUMN_INGREDIENT_NAME, name);
        values.put(COLUMN_INGREDIENT_QUANTITY, quantity);
        values.put(COLUMN_INGREDIENT_UNIT, unit);
        return db.insert(TABLE_INGREDIENTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}