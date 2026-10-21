public class Session {
    public static int userId = -1;
    public static String username = "";
    public static String fullName = "";
    public static String role = "";
    public static String shopName = "";

    public static void clear() {
        userId = -1;
        username = "";
        fullName = "";
        role = "";
        shopName = "";
    }

    public static boolean isAdmin() { return "admin".equals(role); }
    public static boolean isShopOwner() { return "shop_owner".equals(role); }
}