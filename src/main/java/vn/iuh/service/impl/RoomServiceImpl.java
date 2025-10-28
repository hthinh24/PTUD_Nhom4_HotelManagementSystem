package vn.iuh.service.impl;

import vn.iuh.constraint.ActionType;
import vn.iuh.constraint.EntityIDSymbol;
import vn.iuh.dao.CongViecDAO;
import vn.iuh.dao.LichSuThaoTacDAO;
import vn.iuh.dao.LoaiPhongDAO;
import vn.iuh.dao.PhongDAO;
import vn.iuh.dto.event.create.RoomCreationEvent;
import vn.iuh.dto.event.update.RoomModificationEvent;
import vn.iuh.dto.repository.RoomFurnitureItem;
import vn.iuh.entity.CongViec;
import vn.iuh.entity.LichSuThaoTac;
import vn.iuh.entity.LoaiPhong;
import vn.iuh.entity.Phong;
import vn.iuh.gui.base.Main;
import vn.iuh.service.RoomService;
import vn.iuh.util.EntityUtil;

import java.sql.Timestamp;
import java.util.*;

public class RoomServiceImpl implements RoomService {
    private final PhongDAO phongDAO;
    private final CongViecDAO congViecDAO;
    private final LoaiPhongDAO loaiPhongDAO;

    public RoomServiceImpl() {
        phongDAO = new PhongDAO();
        congViecDAO = new CongViecDAO();
        loaiPhongDAO = new LoaiPhongDAO();
    }

    public RoomServiceImpl(PhongDAO phongDAO, CongViecDAO congViecDAO, LoaiPhongDAO loaiPhongDAO) {
        this.phongDAO = phongDAO;
        this.congViecDAO = congViecDAO;
        this.loaiPhongDAO = loaiPhongDAO;
    }

    @Override
    public Phong getRoomByID(String roomID) {
        Phong phong = phongDAO.timPhong(roomID);
        if (phong == null) {
            System.out.println("Room with ID " + roomID + " not found.");
            return null;
        } else {
            return phong;
        }
    }

    @Override
    public List<Phong> getAll() {
        List<Phong> phongs = phongDAO.timTatCaPhong();
        if (phongs.isEmpty()) {
            System.out.println("No rooms found.");
            return null;
        } else {
            return phongs;
        }
    }

    @Override
    public List<RoomFurnitureItem> getAllFurnitureInRoom(String roomID) {
        return phongDAO.timTatCaNoiThatTrongPhong(roomID);
    }

//    @Override
//    public Phong createRoom(RoomCreationEvent room) {
//        Phong lastedPhong = phongDAO.timPhongMoiNhat();
//        String newID = EntityUtil.increaseEntityID(
//                lastedPhong.getMaPhong(),
//                EntityIDSymbol.ROOM_PREFIX.getPrefix(),
//                EntityIDSymbol.ROOM_PREFIX.getLength());
//
//        Phong newPhong = new Phong(
//                newID,
//                room.getRoomName(),
//                true,
//                room.getNote(),
//                room.getRoomDescription(),
//                room.getRoomCategoryId(),
//                new Timestamp(new Date().getTime())
//        );
//
//        return phongDAO.themPhong(newPhong);
//    }

    @Override
    public Phong updateRoom(RoomModificationEvent room) {
        Phong existingPhong = phongDAO.timPhong(room.getId());
        if (existingPhong == null) {
            System.out.println("Room with ID " + room.getId() + " not found.");
            return null;
        }

        // Lưu lại giá trị gốc để so sánh sau khi cập nhật
        String origName = existingPhong.getTenPhong();
        String origNote = existingPhong.getGhiChu();
        String origDesc = existingPhong.getMoTaPhong();
        String origLoai = existingPhong.getMaLoaiPhong();
        boolean origActive = existingPhong.isDangHoatDong();

        // Áp giá trị mới
        existingPhong.setTenPhong(room.getRoomName());
        existingPhong.setDangHoatDong(existingPhong.isDangHoatDong()); // nếu muốn cập nhật trạng thái, thay đổi ở đây
        existingPhong.setGhiChu(room.getNote());
        existingPhong.setMoTaPhong(room.getRoomDescription());
        existingPhong.setMaLoaiPhong(room.getRoomCategoryId());

        Phong updated = phongDAO.capNhatPhong(existingPhong);

        // Cập nhật thành công
        if (updated != null) {
            try {
                LichSuThaoTacDAO whDao = new LichSuThaoTacDAO();

                var last = whDao.timLichSuThaoTacMoiNhat();
                String lastId = (last != null) ? last.getMaLichSuThaoTac() : null;

                String newId = EntityUtil.increaseEntityID(
                        lastId,
                        EntityIDSymbol.WORKING_HISTORY_PREFIX.getPrefix(),
                        EntityIDSymbol.WORKING_HISTORY_PREFIX.getLength()
                );

                LichSuThaoTac wh = new LichSuThaoTac();
                wh.setMaLichSuThaoTac(newId);

                wh.setTenThaoTac(ActionType.EDIT_ROOM.getActionName());
                List<String> changes = new ArrayList<>();

                if (!Objects.equals(origName, updated.getTenPhong())) {
                    changes.add(String.format("Tên: '%s' -> '%s'",
                            origName == null ? "" : origName,
                            updated.getTenPhong() == null ? "" : updated.getTenPhong()));
                }
                if (!Objects.equals(origNote, updated.getGhiChu())) {
                    changes.add(String.format("Ghi chú: '%s' -> '%s'",
                            origNote == null ? "" : origNote,
                            updated.getGhiChu() == null ? "" : updated.getGhiChu()));
                }
                if (!Objects.equals(origDesc, updated.getMoTaPhong())) {
                    changes.add(String.format("Mô tả: '%s' -> '%s'",
                            origDesc == null ? "" : origDesc,
                            updated.getMoTaPhong() == null ? "" : updated.getMoTaPhong()));
                }
                if (!Objects.equals(origLoai, updated.getMaLoaiPhong())) {
                    changes.add(String.format("Loại phòng (mã): '%s' -> '%s'",
                            origLoai == null ? "" : origLoai,
                            updated.getMaLoaiPhong() == null ? "" : updated.getMaLoaiPhong()));
                }
                if (origActive != updated.isDangHoatDong()) {
                    changes.add(String.format("Trạng thái hoạt động: '%s' -> '%s'",
                            origActive ? "Hoạt động" : "Không hoạt động",
                            updated.isDangHoatDong() ? "Hoạt động" : "Không hoạt động"));
                }

                String moTa;
                if (changes.isEmpty()) {
                    moTa = String.format("Cập nhật phòng %s: không có thay đổi nội dung.", updated.getMaPhong());
                } else {
                    moTa = String.format("Cập nhật phòng %s: %s", updated.getMaPhong(), String.join("; ", changes));
                }

                wh.setMoTa(moTa);

                wh.setMaPhienDangNhap(Main.getCurrentLoginSession());

                wh.setThoiGianTao(new Timestamp(new Date().getTime()));

                whDao.themLichSuThaoTac(wh);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return updated;
    }

    @Override
    public boolean deleteRoomByID(String roomID) {
        return phongDAO.xoaPhong(roomID);
    }

    public boolean completeCleaning(String roomID) {
        CongViec congViec = congViecDAO.layCongViecHienTaiCuaPhong(roomID);
        System.out.println("Hoàn tất dọn dẹp phòng: " + roomID);
        return congViecDAO.removeJob(congViec.getMaCongViec());
    }

    // Lấy loại phòng của một phòng
    public LoaiPhong getRoomCategoryByID(String id) {
        try {
            return loaiPhongDAO.getRoomCategoryByID(id);
        } catch (Exception e) {
            System.out.println("RoomServiceImpl.getRoomCategoryByID error: " + e.getMessage());
            return null;
        }
    }

    // Lấy giá mới nhất của loại phòng
    public double[] getLatestPriceForLoaiPhong(String maLoaiPhong) {
        try {
            return phongDAO.getLatestPriceForLoaiPhong(maLoaiPhong);
        } catch (Exception e) {
            System.out.println("RoomServiceImpl.getLatestPriceForLoaiPhong error: " + e.getMessage());
            return new double[] {0.0, 0.0};
        }
    }

    // Lấy công việc hiện tại của phòng
    public CongViec getCurrentJobForRoom(String maPhong) {
        try {
            return congViecDAO.layCongViecHienTaiCuaPhong(maPhong);
        } catch (Exception e) {
            System.out.println("RoomServiceImpl.getCurrentJobForRoom error: " + e.getMessage());
            return null;
        }
    }

    // Lấy trạng thái của phòng
    private String resolveRoomStatus(Phong p) {
        try {
            CongViec cv = congViecDAO.layCongViecHienTaiCuaPhong(p.getMaPhong());
            if (cv != null && cv.getTenTrangThai() != null && !cv.getTenTrangThai().isEmpty()) {
                return cv.getTenTrangThai();
            } else if (!p.isDangHoatDong()) {
                return "Bảo trì";
            } else {
                return "Trống";
            }
        } catch (Exception e) {
            return p.isDangHoatDong() ? "Trống" : "Bảo trì";
        }
    }

    // Lấy số người của phòng
    @Override
    public List<Phong> getRoomsByPeopleCount(int people) {
        List<Phong> all = phongDAO.timTatCaPhong();
        if (all == null) return new ArrayList<>();
        List<Phong> out = new ArrayList<>();
        for (Phong p : all) {
            LoaiPhong lp = null;
            try { lp = loaiPhongDAO.getRoomCategoryByID(p.getMaLoaiPhong()); } catch (Exception ignored) {}
            if (lp != null && lp.getSoLuongKhach() == people && p.isDangHoatDong()) {
                out.add(p);
            }
        }
        return out;
    }

    // Lấy danh sách các phòng có trạng thái ...
    @Override
    public List<Phong> getRoomsByStatus(String status) {
        List<Phong> all = phongDAO.timTatCaPhong();
        if (all == null) return new ArrayList<>();
        List<Phong> out = new ArrayList<>();
        for (Phong p : all) {
            String s = resolveRoomStatus(p);
            if (s != null && s.equalsIgnoreCase(status)) out.add(p);
        }
        return out;
    }

    // Lấy danh sách các phòng thông qua loại phòng
    @Override
    public List<Phong> getRoomsByPhanLoai(String phanLoai) {
        List<Phong> all = phongDAO.timTatCaPhong();
        if (all == null) return new ArrayList<>();
        List<Phong> out = new ArrayList<>();
        for (Phong p : all) {
            LoaiPhong lp = null;
            try { lp = loaiPhongDAO.getRoomCategoryByID(p.getMaLoaiPhong()); } catch (Exception ignored) {}
            if (lp != null && lp.getPhanLoai() != null && lp.getPhanLoai().equalsIgnoreCase(phanLoai) && p.isDangHoatDong()) {
                out.add(p);
            }
        }
        return out;
    }

    // Lấy danh sách tất cả các loại phòng
    @Override
    public List<LoaiPhong> getAllRoomCategories() {
        try {
            return loaiPhongDAO.layTatCaLoaiPhong();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Lấy danh sách tất cả nội thất của loại phòng
    @Override
    public List<RoomFurnitureItem> getFurnitureForLoaiPhong(String maLoaiPhong) {
        try {
            return phongDAO.timNoiThatTheoLoaiPhong(maLoaiPhong);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Lấy ID để thêm phòng
    public String getNextRoomID() {
        try {
            Phong last = phongDAO.timPhongMoiNhat(); // DAO của bạn trả về TOP 1 ORDER BY ma_phong DESC
            String lastId = (last != null) ? last.getMaPhong() : null;
            return EntityUtil.increaseEntityID(lastId, EntityIDSymbol.ROOM_PREFIX.getPrefix(), EntityIDSymbol.ROOM_PREFIX.getLength());
        } catch (Exception e) {
            // phòng cuối cùng không tìm thấy -> tạo ID đầu tiên
            return EntityUtil.increaseEntityID(null, EntityIDSymbol.ROOM_PREFIX.getPrefix(), EntityIDSymbol.ROOM_PREFIX.getLength());
        }
    }

    // Kiểm tra tên phòng đã tồn tại chưa
    public boolean isRoomNameExists(String tenPhong) {
        try {
            return phongDAO.timPhongTheoTen(tenPhong) != null;
        } catch (Exception e) {
            return false;
        }
    }

    // Tạo phòng mới
    @Override
    public Phong createRoom(RoomCreationEvent room) {
        // 1. Sinh ID
        String newID = getNextRoomID();

        // 2. Tạo entity Phong và gọi DAO
        Phong newPhong = new Phong(
                newID,
                room.getRoomName(),
                true,
                room.getNote(),
                room.getRoomDescription(),
                room.getRoomCategoryId(),
                new Timestamp(new Date().getTime())
        );

        Phong inserted = phongDAO.themPhong(newPhong);

        // 3. Nếu insert thành công ===> ghi lịch sử thao tác
        if (inserted != null) {
            try {
                LichSuThaoTacDAO whDao = new LichSuThaoTacDAO();
                var last = whDao.timLichSuThaoTacMoiNhat();
                String lastId = (last != null) ? last.getMaLichSuThaoTac() : null;
                String newWhId = EntityUtil.increaseEntityID(lastId,
                        EntityIDSymbol.WORKING_HISTORY_PREFIX.getPrefix(),
                        EntityIDSymbol.WORKING_HISTORY_PREFIX.getLength());

                LichSuThaoTac wh = new LichSuThaoTac();
                wh.setMaLichSuThaoTac(newWhId);
                wh.setTenThaoTac(ActionType.CREATE_ROOM.getActionName());

                // Tạo mô tả chi tiết
                String maLoai = room.getRoomCategoryId();
                double[] price = {0.0, 0.0};
                try { price = getLatestPriceForLoaiPhong(maLoai); } catch (Exception ignored) {}

                String moTa = String.format("Thêm phòng %s: Tên='%s', Loại='%s', Giá ngày=%.0f, Giá giờ=%.0f",
                        inserted.getMaPhong(),
                        inserted.getTenPhong(),
                        maLoai == null ? "" : maLoai,
                        price[0],
                        price[1]);

                wh.setMoTa(moTa);
                wh.setMaPhienDangNhap(Main.getCurrentLoginSession());
                wh.setThoiGianTao(new Timestamp(new Date().getTime()));

                whDao.themLichSuThaoTac(wh);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return inserted;
    }

    // Xóa phòng
    public boolean deleteRoomWithHistory(String maPhong) {
        if (maPhong == null || maPhong.isBlank()) return false;

        try {
            // Lấy thông tin phòng trước khi xóa để lưu mô tả lịch sử
            Phong phong = phongDAO.timPhong(maPhong);
            if (phong == null) {
                System.out.println("deleteRoomWithHistory: Không tìm thấy phòng: " + maPhong);
                return false;
            }

            // Thực hiện xóa
            boolean deleted = phongDAO.xoaPhongQuanLyPhongPanel(maPhong);
            if (!deleted) {
                System.out.println("deleteRoomWithHistory: xóa thất bại cho: " + maPhong);
                return false;
            }

            // Nếu xóa thành công ==> ghi lịch sử thao tác
            try {
                LichSuThaoTacDAO whDao = new LichSuThaoTacDAO();
                var last = whDao.timLichSuThaoTacMoiNhat();
                String lastId = (last != null) ? last.getMaLichSuThaoTac() : null;
                String newWhId = EntityUtil.increaseEntityID(
                        lastId,
                        EntityIDSymbol.WORKING_HISTORY_PREFIX.getPrefix(),
                        EntityIDSymbol.WORKING_HISTORY_PREFIX.getLength()
                );

                LichSuThaoTac wh = new LichSuThaoTac();
                wh.setMaLichSuThaoTac(newWhId);
                wh.setTenThaoTac(ActionType.DELETE_ROOM.getActionName());

                // Tạo mô tả chi tiết
                String moTa = String.format("Xóa phòng %s: Tên='%s', Loại='%s'",
                        phong.getMaPhong(),
                        phong.getTenPhong() == null ? "" : phong.getTenPhong(),
                        phong.getMaLoaiPhong() == null ? "" : phong.getMaLoaiPhong());
                wh.setMoTa(moTa);

                wh.setMaPhienDangNhap(Main.getCurrentLoginSession());
                wh.setThoiGianTao(new Timestamp(new Date().getTime()));

                whDao.themLichSuThaoTac(wh);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách tất cả các phòng
    @Override
    public List<Phong> getAllQuanLyPhongPanel() {
        List<Phong> phongs = phongDAO.timTatCaPhongChoQuanLyPhong();
        if (phongs.isEmpty()) {
            System.out.println("No rooms found.");
            return null;
        } else {
            return phongs;
        }
    }

}