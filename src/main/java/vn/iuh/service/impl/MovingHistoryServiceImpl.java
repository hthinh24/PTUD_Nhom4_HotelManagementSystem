package vn.iuh.service.impl;

import vn.iuh.constraint.EntityIDSymbol;
import vn.iuh.dao.LichSuDiVaoDAO;
import vn.iuh.dao.LichSuRaNgoaiDAO;
import vn.iuh.entity.LichSuDiVao;
import vn.iuh.entity.LichSuRaNgoai;
import vn.iuh.service.MovingHistoryService;
import vn.iuh.util.EntityUtil;

public class MovingHistoryServiceImpl implements MovingHistoryService {
    private final LichSuDiVaoDAO lichSuDiVaoDAO;
    private final LichSuRaNgoaiDAO lichSuRaNgoaiDAO;

    public MovingHistoryServiceImpl() {
        this.lichSuDiVaoDAO = new LichSuDiVaoDAO();
        this.lichSuRaNgoaiDAO = new LichSuRaNgoaiDAO();
    }

    public MovingHistoryServiceImpl(LichSuDiVaoDAO lichSuDiVaoDAO, LichSuRaNgoaiDAO lichSuRaNgoaiDAO) {
        this.lichSuDiVaoDAO = lichSuDiVaoDAO;
        this.lichSuRaNgoaiDAO = lichSuRaNgoaiDAO;
    }

    @Override
    public boolean createEnteringHistory(String maChiTietDatPhong) {
        LichSuDiVao lichSuDiVao = lichSuDiVaoDAO.timLichSuDiVaoMoiNhat();
        String maLichSuDiVao = lichSuDiVao != null ? lichSuDiVao.getMaLichSuDiVao() : null;

        return lichSuDiVaoDAO.themLichSuDiVao(new LichSuDiVao(
                EntityUtil.increaseEntityID(maLichSuDiVao, EntityIDSymbol.HISTORY_CHECKIN_PREFIX.getPrefix(), EntityIDSymbol.HISTORY_CHECKIN_PREFIX.getLength()),
                false,
                maChiTietDatPhong,
                null
        ));
    }

    @Override
    public boolean createLeavingHistory(String maChiTietDatPhong) {
        LichSuRaNgoai lichSuRaNgoai = lichSuRaNgoaiDAO.timLichSuRaNgoaiMoiNhat();
        String maLichSuRaNgoai = lichSuRaNgoai != null ? lichSuRaNgoai.getMaLichSuRaNgoai() : null;

        return lichSuRaNgoaiDAO.themLichSuRaNgoai(new LichSuRaNgoai(
                EntityUtil.increaseEntityID(maLichSuRaNgoai, EntityIDSymbol.HISTORY_CHECKOUT_PREFIX.getPrefix(), EntityIDSymbol.HISTORY_CHECKOUT_PREFIX.getLength()),
                false,
                maChiTietDatPhong,
                null
        )) != null;
    }
}
