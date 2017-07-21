package by.gto.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Observable;

public class VatData {
    private int blancTsInfoId;
    private Integer vatId;
    private SimpleStringProperty vatFullNumber;
    private SimpleStringProperty date;

    private Integer vatUnp;
    private Short vatYear;
    private Long vatNumber;

    private Date _date;
    private SimpleIntegerProperty contractorUnp;
    private SimpleStringProperty contractorName;
    private SimpleStringProperty agreementNumber;
    private SimpleObjectProperty<Date> agreementDate;
    private BigDecimal withoutVAT;
    private BigDecimal withVAT;
    private BigDecimal VAT;

    private SimpleStringProperty blankSeries;
    private SimpleStringProperty blankNumber;
    private SimpleIntegerProperty vatState;
    private SimpleObjectProperty<Integer> branch;
    private ObservableList<AgreementData> agreementOptions = null;
    private ObservableList<BranchInfo> branches = null;


    public VatData(int blancTsInfoId, Integer vatId,
                   String blankSeries, Integer blankNumber,
                   Integer vatUnp, Short vatYear, Long vatNumber,
                   Date date, int contractorUnp, String contractorName, BigDecimal withoutVAT,
                   BigDecimal withVAT, BigDecimal VAT, int vatState, Integer branch,
                   String agreementNumber, Date agreementDate) {
        this.blancTsInfoId = blancTsInfoId;
        this.vatId = vatId;
        this.blankSeries = new SimpleStringProperty(blankSeries);
        this.blankNumber = new SimpleStringProperty(String.valueOf(blankNumber));
        this.vatUnp = vatUnp;
        this.vatYear = vatYear;
        this.vatNumber = vatNumber;
        this.date = new SimpleStringProperty(String.format("%1$td.%1$tm.%1$tY", date));
        this._date = date;
        this.contractorUnp = new SimpleIntegerProperty(contractorUnp);
        this.contractorName = new SimpleStringProperty(contractorName);
        this.withoutVAT = withoutVAT;
        this.withVAT = withVAT;
        this.VAT = VAT;
        this.vatState = new SimpleIntegerProperty(vatState);
//        this.hasBranches = hasBranches;
        this.branch = new SimpleObjectProperty<>(branch);
        this.agreementNumber = new SimpleStringProperty(agreementNumber);
        this.agreementDate = new SimpleObjectProperty<>(agreementDate);

        if (isVatIssued()) {
            this.vatFullNumber = new SimpleStringProperty(
                    String.format("%09d-%04d-%010d", vatUnp, vatYear, vatNumber));
        } else {
            this.vatFullNumber = new SimpleStringProperty(null);
        }
    }

    public int getBlancTsInfoId() {
        return blancTsInfoId;
    }

    public void setBlancTsInfoId(int blancTsInfoId) {
        this.blancTsInfoId = blancTsInfoId;
    }


    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public int getContractorUnp() {
        return contractorUnp.get();
    }

    public void setContractorUnp(int contractorUnp) {
        this.contractorUnp.set(contractorUnp);
    }

    public SimpleIntegerProperty contractorUnpProperty() {
        return contractorUnp;
    }

    public String getContractorName() {
        return contractorName.get();
    }

    public void setContractorName(String contractorName) {
        this.contractorName.set(contractorName);
    }

    public SimpleStringProperty contractorNameProperty() {
        return contractorName;
    }

    public String getVatFullNumber() {
        return vatFullNumber.get();
    }

    public void setVatFullNumber(String vatFullNumber) {
        this.vatFullNumber.set(vatFullNumber);
    }

    public SimpleStringProperty vatFullNumberProperty() {
        return vatFullNumber;
    }

    public Date get_date() {
        return _date;
    }

    public void set_date(Date _date) {
        this._date = _date;
    }

    public boolean isVatIssued() {
        return (vatUnp != null && vatYear != null && vatNumber != null);
    }

    public Integer getVatUnp() {
        return vatUnp;
    }

    public void setVatUnp(Integer vatUnp) {
        this.vatUnp = vatUnp;
    }

    public Short getVatYear() {
        return vatYear;
    }

    public void setVatYear(Short vatYear) {
        this.vatYear = vatYear;
    }

    public Long getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(Long vatNumber) {
        this.vatNumber = vatNumber;
    }

    public Integer getVatId() {
        return vatId;
    }

    public void setVatId(Integer vatId) {
        this.vatId = vatId;
    }

    public String getBlankSeries() {
        return blankSeries.get();
    }

    public SimpleStringProperty blankSeriesProperty() {
        return blankSeries;
    }

    public void setBlankSeries(String blankSeries) {
        this.blankSeries.set(blankSeries);
    }

    public String getBlankNumber() {
        return blankNumber.get();
    }

    public SimpleStringProperty blankNumberProperty() {
        return blankNumber;
    }

    public void setBlankNumber(String blankNumber) {
        this.blankNumber.set(blankNumber);
    }

    public int getVatState() {
        return vatState.get();
    }

    public SimpleIntegerProperty vatStateProperty() {
        return vatState;
    }

    public void setVatState(int vatState) {
        this.vatState.set(vatState);
    }

    public BigDecimal getVAT() {
        return VAT;
    }

    public void setVAT(BigDecimal VAT) {
        this.VAT = VAT;
    }

    public BigDecimal getWithoutVAT() {
        return withoutVAT;
    }

    public void setWithoutVAT(BigDecimal withoutVAT) {
        this.withoutVAT = withoutVAT;
    }

    public BigDecimal getWithVAT() {
        return withVAT;
    }

    public void setWithVAT(BigDecimal withVAT) {
        this.withVAT = withVAT;
    }

    public boolean isHasBranches() {
        return branches != null && branches.size() > 0;
    }

//    public void setHasBranches(boolean hasBranches) {
//        this.hasBranches = hasBranches;
//    }

    public String getAgreementNumber() {
        return agreementNumber.get();
    }

    public SimpleStringProperty agreementNumberProperty() {
        return agreementNumber;
    }

    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber.set(agreementNumber);
    }

    public ObservableList<AgreementData> getAgreementOptions() {
        return agreementOptions;
    }

    public void setAgreementOptions(ObservableList<AgreementData> agreementOptions) {
        this.agreementOptions = agreementOptions;
    }

    public Date getAgreementDate() {
        return agreementDate.get();
    }

    public SimpleObjectProperty<Date> agreementDateProperty() {
        return agreementDate;
    }

    public void setAgreementDate(Date agreementDate) {
        this.agreementDate.set(agreementDate);
    }

    public ObservableList<BranchInfo> getBranches() {
        return branches;
    }

    public void setBranches(ObservableList<BranchInfo> branches) {
        this.branches = branches;
    }


    public Integer getBranch() {
        return branch.get();
    }

    public SimpleObjectProperty<Integer> branchProperty() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch.set(branch);
    }
}
