package avatar.server;

public class GameString {

    public static String leave1() {
        return "Đã bỏ chạy -%dxp.";
    }

    public static String leave2() {
        return "Đã bỏ chạy.";
    }

    public static String inviteError1() {
        return "Bạn đó đã offline rồi!.";
    }

    public static String inviteError2() {
        return "Bạn đó đã vào bàn khác chơi rồi!.";
    }

    public static String inviteMessage() {
        return "%s mời bạn chơi?";
    }

    public static String missionError1() {
        return "Không tồn tại nhiệm vụ này!";
    }

    public static String missionError2() {
        return "Nhiệm vụ chưa hoàn thành!";
    }

    public static String missionError3() {
        return "Nhiệm vụ đã hoàn thành!";
    }

    public static String missionComplete() {
        return "Chúc mừng bạn nhận được phần thưởng %s!";
    }

    public static String phucHoiSuccess() {
        return "Phục hồi điểm nâng cấp thành công!.";
    }

    public static String x2XPSuccess() {
        return "Sử dụng thành công. Bạn có 1 ngày xp x2!.";
    }

    public static String x2XPRequest() {
        return "Bạn có muốn sử dụng item này không? Hiệu lực 1 ngày!.";
    }

    public static String phucHoiDiemString() {
        return "Bạn có muốn sử dụng item này không? Sẽ xóa hết điểm đã nâng!.";
    }

    public static String mssTGString() {
        return "Tin nhắn từ %s: %s.";
    }

    public static String thongBaoString() {
        return "Thông Báo: %s.";
    }

    public static String dailyReward() {
        return "Hôm nay bạn được tặng %dx item %s. Chúc chơi game vui vẻ.";
    }

    public static String newsNoti() {
        return "Ủng hộ cho game soạn tin nhắn: KN UH %s gửi 8798. Thay lời cám ơn, chúng tôi sẽ tặng bạn biểu tượng trên nhân vật !";
    }

    public static String loginErr1() {
        return "Bạn đang đăng nhập ở máy khác. Hãy thử đăng nhập lại";
    }

    public static String userLoginLock(final int minutes) {
        return "Tài khoản của bạn đang bị khóa trong " + (minutes + 1) + " phút, vui lòng liên hệ admin để biết thêm chi tiết !";
    }

    public static String userLoginLockForever() {
        return "Tài khoản của bạn đã bị khóa trong vĩnh viễn, vui lòng liên hệ admin để biết thêm chi tiết !";
    }

    public static String userChatLock(final int minutes) {
        return "Tài khoản của bạn đang bị khóa chat thế giới trong " + (minutes + 1) + " phút !";
    }

    public static String userChatLockForever() {
        return "Tài khoản của bạn đã bị khóa chat thế giới trong vĩnh viễn !";
    }

    public static String userLoginActive() {
        return "Tài khoản chưa kích hoạt! Vui lòng kích hoạt để trải nghiệm.";
    }

    public static String userLoginMany() {
        return "Có người khác đăng nhập vào tài khoản của bạn!.";
    }

    public static String loginPassFail() {
        return "Thông tin tài khoản hoặc mật khẩu không chính xác";
    }

    public static String hopNgocFail() {
        return "Chế ngọc thất bại, hao phí %d %s!.";
    }

    public static String hopNgocSucess() {
        return "Chế ngọc thành công hao phí %d %s thu được %d %s!.";
    }

    public static String cheDoSuccess() {
        return "Chế đồ thành công";
    }

    public static String cheDoFail() {
        return "Chế đồ thất bại";
    }

    public static String hopNgocError() {
        return "Lỗi kết hợp";
    }

    public static String hopNgocCantDo() {
        return "Không thể kết hợp";
    }

    public static String hopNgocRequest() {
        return "Bạn có muốn gắn ngọc vào trang bị?";
    }

    public static String hopNgocNC() {
        return "Bạn có muốn hợp thành ngọc cấp cao hơn? Tỉ lệ thành công là %s.";
    }

    public static String hopNgocSell() {
        return "Bạn có muốn bán %d vật phẩm với giá %d xu?";
    }

    public static String hopNgocNoSlot() {
        return "Trang bị đã chọn không còn đủ chỗ";
    }

    public static String itemNotAvailable() {
        return "Vật phẩm này không bán, vui lòng quay lại sau !.";
    }

    public static String xuNotEnought() {
        return "Bạn không có đủ tiền!.";
    }

    public static String levelNotEnought() {
        return "Biệt đội của bạn không đủ cấp độ để mua vật phẩm này ! Hãy liên hệ đội trưởng để nâng cấp !";
    }

    public static String buySuccess() {
        return "Giao dịch thành công. Xin cảm ơn!.";
    }

    public static String changPassError1() {
        return "Mật khẩu cũ không chính xác!.";
    }

    public static String ruongNoSlot() {
        return "Không đủ chỗ trong rương!.";
    }

    public static String thaoNgocRequest() {
        return "Bạn có muốn tháo ngọc khỏi trang bị? Bạn cần %d xu?";
    }

    public static String sellTBRequest() {
        return "Bạn có muốn bán trang bị này với giá %d xu?";
    }

    public static String sellTBError1() {
        return "Không thể bán trang bị đang sử dụng!.";
    }

    public static String thaoNgocError1() {
        return "Không thể tháo ngọc trang bị đang sử dụng!.";
    }

    public static String thaoNgocError2() {
        return "Bạn không có đủ tiền. Bạn cần %d xu để thực hiện thao tác này!.";
    }

    public static String thaoNgocSuccess() {
        return "Thao tác thành công!.";
    }

    public static String giaHanRequest() {
        return "Bạn có muốn gia hạn trang bị này với giá %d xu?";
    }

    public static String giaHanSucess() {
        return "Gia hạn thành công!.";
    }

    public static String findKVError1() {
        return "Không tìm được khu vực!.";
    }

    public static String joinKVError1() {
        return "Mật khẩu không chính xác!.";
    }

    public static String joinKVError2() {
        return "Không đủ tiền cược!.";
    }

    public static String joinKVError3() {
        return "Khu vực đã đầy!.";
    }

    public static String kickString() {
        return "Bạn bị kick bởi chủ phòng!.";
    }

    public static String datCuocError1() {
        return "Chỉ có thể đặt cược từ %d xu đến %d xu!.";
    }

    public static String startGameError1() {
        return "Mọi người chưa sẵn sàng!.";
    }

    public static String startGameError2() {
        return "Còn %s chưa sẵn sàng!.";
    }

    public static String startGameError3() {
        return "%s lỗi item slot %d xin chọn lại!.";
    }

    public static String startGameError4() {
        return "Số lượng 2 bên chưa ngang nhau";
    }

    public static String selectMapError1_1() {
        return "Chỉ có thể chọn map %s!.";
    }

    public static String selectMapError1_2() {
        return "Chỉ có thể chọn map %s hoặc map %s!.";
    }

    public static String selectMapError1_3() {
        return "Lỗi chọn map!.";
    }
}
