package org.rocs.osd.controller.student;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.rocs.osd.data.dao.disciplinary.status.DisciplinaryStatusDao;
import org.rocs.osd.data.dao.disciplinary.status.impl.DisciplinaryStatusDaoImpl;
import org.rocs.osd.data.dao.enrollment.impl.EnrollmentDaoImpl;
import org.rocs.osd.data.dao.guardian.GuardianDao;
import org.rocs.osd.data.dao.guardian.impl.GuardianDaoImpl;
import org.rocs.osd.data.dao.record.impl.RecordDaoImpl;
import org.rocs.osd.data.dto.student.report.StudentReportDTO;
import org.rocs.osd.facade.enrollment.EnrollmentFacade;
import org.rocs.osd.facade.enrollment.impl.EnrollmentFacadeImpl;
import org.rocs.osd.facade.record.RecordFacade;
import org.rocs.osd.facade.record.impl.RecordFacadeImpl;
import org.rocs.osd.model.disciplinary.status.DisciplinaryStatus;
import org.rocs.osd.model.enrollment.Enrollment;
import org.rocs.osd.model.person.guardian.Guardian;
import org.rocs.osd.model.person.student.guardian.StudentGuardian;
import org.rocs.osd.model.record.Record;

import java.io.File;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the student record detail view.
 */
public class StudentRecordController {

    /** Text field for full name. */
    @FXML
    private TextField fullNameTextField;

    /** Dropdown for grade or section. */
    @FXML
    private ComboBox<String> gradeComboBox;

    /** Dropdown for disciplinary action status. */
    @FXML
    private ComboBox<String> statusComboBox;

    /** Text field for section. */
    @FXML
    private TextField sectionTextField;

    /** Text field for academic year. */
    @FXML
    private TextField academicYearTextField;

    /** Text field for full name of guardian. */
    @FXML
    private TextField guardianTextField;

    /** Text field for contact number. */
    @FXML
    private TextField contactNumberTextField;

    /** Text field for address. */
    @FXML
    private TextField addressTextField;

    /** Check box for intern. */
    @FXML
    private CheckBox internCheckBox;

    /** Check box for extern. */
    @FXML
    private CheckBox externCheckBox;

    /**
     * Checkbox for saving current
     * selected value on statusComboBox.
     */
    @FXML
    private CheckBox statusSaveCheckbox;

    /** Table for history offense. */
    @FXML
    private TableView<Record> offenseHistoryTable;

    /** Table column of offense type. */
    @FXML
    private TableColumn<Record, String> offenseTypeColumn;

    /** Table column of offense level. */
    @FXML
    private TableColumn<Record, String> levelOfOffenseColumn;

    /** Table column of date. */
    @FXML
    private TableColumn<Record, Date> dateColumn;

    /** Object for enrollment model. */
    private Enrollment enrollment;

    /** DAO for guardian. */
    private GuardianDao guardianDao;

    /** Facade for record. */
    private RecordFacade recordFacade;

    /** Handler for download action. */
    private Runnable downloadHandler;

    /**
     * Controller Class Instance.
     */
    private StudentController studentController;

    /** Facade for Enrollment. */
    private EnrollmentFacade enrollmentFacade;

    /** DAO for Disciplinary Status. */
    private DisciplinaryStatusDao disciplinaryStatusDao;

    /**
     * Stores the disciplinary statuses returned by the query.
     */
    private List<DisciplinaryStatus> arrayStatus;

    /**
     * First code to run when the class is initializing.
     */
    @FXML
    public void initialize() {
        if (enrollmentFacade == null) {
            enrollmentFacade =
                    new EnrollmentFacadeImpl(new EnrollmentDaoImpl());
        }

        if (disciplinaryStatusDao == null) {
            disciplinaryStatusDao =
                    new DisciplinaryStatusDaoImpl();
        }

        if (gradeComboBox != null) {
            gradeComboBox.setOnAction(event ->
                    setOffenseDataByStudentLevel());
        }

        if (statusComboBox != null) {
            statusComboBox.valueProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        if (oldValue != null
                                && !oldValue.equals(newValue)) {
                            statusSaveCheckbox.setSelected(false);
                        }
                    }
            );
        }
    }

    /**
     * Sets the enrollment facade.
     *
     * @param facade the enrollment facade to use
     */
    public void setEnrollmentFacade(EnrollmentFacade facade) {
        this.enrollmentFacade = facade;
    }

    /**
     * Sets the guardian DAO.
     *
     * @param dao the DAO to use
     */
    public void setGuardianDao(GuardianDao dao) {
        this.guardianDao = dao;
    }

    /**
     * Sets the disciplinary status DAO used by this controller.
     *
     * @param mDisciplinaryStatusDao the disciplinary status DAO to use
     */
    public void setDisciplinaryStatusDao(
            DisciplinaryStatusDao mDisciplinaryStatusDao) {
        this.disciplinaryStatusDao = mDisciplinaryStatusDao;
    }

    /**
     * Sets the record facade.
     *
     * @param facade the facade to use
     */
    public void setRecordFacade(RecordFacade facade) {
        this.recordFacade = facade;
    }

    /**
     * Sets the download handler for testing.
     *
     * @param handler the handler to run
     */
    public void setDownloadHandler(Runnable handler) {
        this.downloadHandler = handler;
    }

    /**
     * Gets the guardian DAO, creating the default implementation
     * if one has not been provided.
     *
     * @return guardian DAO
     */
    private GuardianDao getGuardianDao() {
        if (guardianDao == null) {
            guardianDao = new GuardianDaoImpl();
        }
        return guardianDao;
    }

    /**
     * Gets the record facade, creating the default implementation
     * if one has not been provided.
     *
     * @return record facade
     */
    private RecordFacade getRecordFacade() {
        if (recordFacade == null) {
            recordFacade =
                    new RecordFacadeImpl(new RecordDaoImpl());
        }
        return recordFacade;
    }

    /**
     * Sets the student enrollment data.
     *
     * @param studentEnrollment the enrollment object
     */
    public void setStudentData(Enrollment studentEnrollment) {
        this.enrollment = studentEnrollment;
        loadData();
        setOffenseData();
    }

    /**
     * Sets the StudentController instance to activate
     * the refresh method for the table view.
     *
     * @param pstudentController the StudentController instance
     *                           to be used
     */
    public void setStudentController(
            StudentController pstudentController) {
        studentController = pstudentController;
    }

    /**
     * Loads student information into UI components.
     */
    public void loadData() {
        if (enrollment == null) {
            return;
        }

        fullNameTextField.setText(
                enrollment.getStudent().getFirstName()
                        + " "
                        + enrollment.getStudent().getMiddleName()
                        + " "
                        + enrollment.getStudent().getLastName()
        );

        gradeComboBox.setValue(
                toDisplayGradeLevel(
                        enrollment.getStudentLevel()
                )
        );

        sectionTextField.setText(
                enrollment.getSection()
        );

        academicYearTextField.setText(
                enrollment.getSchoolYear()
        );

        addressTextField.setText(
                enrollment.getStudent().getAddress()
        );

        String studentType =
                enrollment.getStudent().getStudentType();

        String studentId =
                enrollment.getStudent().getStudentId();

        List<StudentGuardian> guardian =
                getGuardianDao().findGuardianByStudentId(studentId);

        if (guardian != null && !guardian.isEmpty()) {
            Guardian primaryGuardian =
                    guardian.get(0).getGuardian();

            guardianTextField.setText(
                    primaryGuardian.getFirstName()
                            + " "
                            + primaryGuardian.getLastName()
            );

            contactNumberTextField.setText(
                    primaryGuardian.getContactNumber()
            );
        } else {
            guardianTextField.clear();
            contactNumberTextField.clear();
        }

        if (enrollment.getDisciplinaryStatus() != null) {
            statusComboBox.setValue(
                    enrollment.getDisciplinaryStatus().getStatus()
            );
        } else {
            statusComboBox.setValue("");
        }

        if (disciplinaryStatusDao != null) {
            arrayStatus =
                    disciplinaryStatusDao
                            .getAllDisciplinaryStatus();
        }

        if (statusComboBox.getItems().isEmpty()
                && arrayStatus != null) {

            for (DisciplinaryStatus status : arrayStatus) {
                statusComboBox.getItems().add(
                        status.getStatus()
                );
            }
        }

        statusSaveCheckbox.setSelected(false);

        internCheckBox.setMouseTransparent(true);
        externCheckBox.setMouseTransparent(true);
        statusSaveCheckbox.setMouseTransparent(true);

        if ("Intern".equalsIgnoreCase(studentType)) {
            internCheckBox.setSelected(true);
            externCheckBox.setSelected(false);
        } else if ("Extern".equalsIgnoreCase(studentType)) {
            internCheckBox.setSelected(false);
            externCheckBox.setSelected(true);
        } else {
            internCheckBox.setSelected(false);
            externCheckBox.setSelected(false);
        }
    }

    /**
     * Updates the selected disciplinary status for a student's
     * enrollment in the specified school year.
     *
     * @param statusID the ID of the disciplinary status
     * @param studentID the unique identifier of the student
     * @param schoolYear the school year of the student's enrollment
     */
    private void selectedStatus(
            long statusID,
            String studentID,
            String schoolYear) {

        enrollmentFacade.setDisciplinaryStatusID(
                statusID,
                studentID,
                schoolYear
        );
    }

    /**
     * Triggered when the user selects a student's grade level.
     * Displays the student's offenses for the selected grade level.
     * If the student was not enrolled during that school year,
     * the student's information is displayed accordingly.
     */
    private void setOffenseDataByStudentLevel() {
        if (gradeComboBox == null
                || gradeComboBox.getValue() == null
                || gradeComboBox.getValue().isBlank()
                || enrollment == null) {
            return;
        }

        Enrollment studentInfo =
                enrollmentFacade
                        .getEnrollmentsByStudentLevelAndName(
                                toDatabaseGradeLevel(
                                        gradeComboBox.getValue()
                                ),
                                enrollment.getStudent()
                                        .getFirstName(),
                                enrollment.getStudent()
                                        .getMiddleName(),
                                enrollment.getStudent()
                                        .getLastName()
                        );

        if (studentInfo == null) {
            offenseHistoryTable.getItems().clear();
            statusComboBox.setValue("");
            academicYearTextField.setText("");
            sectionTextField.setText("");
            return;
        }

        enrollment = studentInfo;
        loadData();
        setOffenseData();
    }

    /**
     * Loads offense history into the table.
     */
    private void setOffenseData() {
        if (enrollment == null
                || gradeComboBox == null
                || gradeComboBox.getValue() == null) {
            return;
        }

        List<Record> records =
                getRecordFacade()
                        .getRecordByStudentLevel(
                                toDatabaseGradeLevel(
                                        gradeComboBox.getValue()
                                ),
                                enrollment.getStudent()
                                        .getFirstName(),
                                enrollment.getStudent()
                                        .getMiddleName(),
                                enrollment.getStudent()
                                        .getLastName()
                        );

        loadOffenseHistory(records);
    }

    /**
     * Converts the displayed grade level into the database format.
     *
     * @param studentLevel displayed grade level
     * @return database-compatible grade level
     */
    private String toDatabaseGradeLevel(String studentLevel) {
        if (studentLevel == null) {
            return null;
        }

        if (studentLevel.contains("Grade")) {
            studentLevel =
                    studentLevel.replace(" ", "-");
        }

        return studentLevel;
    }

    /**
     * Converts the database grade level into the display format.
     *
     * @param studentLevel database grade level
     * @return displayed grade level
     */
    private String toDisplayGradeLevel(String studentLevel) {
        if (studentLevel == null) {
            return null;
        }

        if (studentLevel.contains("Grade")) {
            studentLevel =
                    studentLevel.replace("-", " ");
        }

        return studentLevel;
    }

    /**
     * Loads offense history into the table.
     *
     * @param records list of student disciplinary records
     */
    public void loadOffenseHistory(List<Record> records) {
        offenseTypeColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue()
                                .getOffense()
                                .getOffense()
                )
        );

        levelOfOffenseColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue()
                                .getOffense()
                                .getType()
                )
        );

        dateColumn.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(
                        new Date(
                                cell.getValue()
                                        .getDateOfViolation()
                                        .getTime()
                        )
                )
        );

        offenseHistoryTable.setItems(
                FXCollections.observableArrayList(records)
        );
    }

    /**
     * Creates the file chooser used when downloading
     * the student's discipline report.
     *
     * @return configured file chooser
     */
    private FileChooser getFileChooser() {
        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Save Discipline Sheet"
        );

        FileChooser.ExtensionFilter pdfFilter =
                new FileChooser.ExtensionFilter(
                        "PDF Files (*.pdf)",
                        "*.pdf"
                );

        fileChooser.getExtensionFilters()
                .add(pdfFilter);

        fileChooser.setSelectedExtensionFilter(
                pdfFilter
        );

        String studentId =
                enrollment.getStudent()
                        .getStudentId();

        String surname =
                enrollment.getStudent()
                        .getLastName();

        String defaultFileName =
                studentId
                        + "_"
                        + surname
                        + "_Discipline_Report.pdf";

        fileChooser.setInitialFileName(
                defaultFileName
        );

        return fileChooser;
    }

    /**
     * Downloads the selected student data to desired directory.
     * Automatically opens file when the user downloads the PDF.
     */
    @FXML
    public void onDownload() {
        if (downloadHandler != null) {
            downloadHandler.run();
            return;
        }

        FileChooser fileChooser = getFileChooser();
        new FileChooser.ExtensionFilter("PDF Files", "*.pdf");

        String downloads = System.getProperty("user.home")
                + File.separator + "Downloads";
        File downloadDir = new File(downloads);

        if (downloadDir.exists()) {
            fileChooser.setInitialDirectory(downloadDir);
        }

        Stage stage = (Stage) fullNameTextField.getScene().getWindow();
        File outputFile = fileChooser.showSaveDialog(stage);

        if (outputFile != null) {
            try (InputStream reportStream = getClass().getResourceAsStream(
                    "/reports/StudentReport.jasper")) {

                List<Record> currentRecord =
                        new ArrayList<>(offenseHistoryTable.getItems());

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("studentName", fullNameTextField.getText());
                parameters.put("grade", gradeComboBox.getValue());
                parameters.put("section", sectionTextField.getText());
                parameters.put("academicYear", academicYearTextField.getText());
                parameters.put("studentAddress", addressTextField.getText());
                parameters.put("guardianName", guardianTextField.getText());
                parameters.put("contactNumber",
                        contactNumberTextField.getText());
                parameters.put("guardianAddress", addressTextField.getText());

                if (statusSaveCheckbox.isSelected()) {
                    parameters.put("status", statusComboBox.getValue());
                } else {
                    parameters.put("status", enrollment
                            .getDisciplinaryStatus()
                            .getStatus());
                }

                parameters.put("internCheckBox",
                        internCheckBox.isSelected() ? "X" : "");
                parameters.put("externCheckBox",
                        externCheckBox.isSelected() ? "X" : "");

                try (InputStream logo = getClass().getResourceAsStream(
                        "/reports/logo.png")) {
                    if (logo != null) {
                        parameters.put("logoStream", logo);
                    }

                    List<StudentReportDTO> tableData = new ArrayList<>();
                    for (Record record : currentRecord) {
                        StudentReportDTO row =
                                new StudentReportDTO();
                        row.setOffenseType(
                                record.getOffense()
                                        .getOffense());
                        row.setLevelOfOffense(
                                record.getOffense()
                                        .getType());
                        row.setDate(record.getDateOfViolation()
                                .toString());
                        tableData.add(row);
                    }

                    JRDataSource dataSource;

                    if (tableData.isEmpty()) {
                        dataSource = new JREmptyDataSource(1);
                    } else {
                        dataSource = new JRBeanCollectionDataSource(tableData);
                    }

                    JasperPrint jasperPrint = JasperFillManager.fillReport(
                            reportStream, parameters, dataSource
                    );

                    JasperExportManager.exportReportToPdfFile(
                            jasperPrint, outputFile .getAbsolutePath()
                    );

                    if (java.awt.Desktop.isDesktopSupported()) {
                        java.awt.Desktop.getDesktop() .open(outputFile);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes the current window.
     *
     * @param event the action event
     */
    @FXML
    public void onCancel(ActionEvent event) {
        Stage stage =
                (Stage) ((Node) event.getSource())
                        .getScene()
                        .getWindow();

        stage.close();
    }

    /**
     * Save the set Status made by the prefect, on
     * a particular student.
     *
     * @param e action event for the button
     */
    @FXML
    public void saveStatus(ActionEvent e) {
        if (enrollment == null
                || arrayStatus == null
                || statusComboBox.getValue() == null
                || statusComboBox.getValue().isBlank()) {
            return;
        }

        for (DisciplinaryStatus status : arrayStatus) {
            if (statusComboBox.getValue().equals(status.getStatus())) {

                selectedStatus(
                        status.getDisciplinaryStatusId(),
                        enrollment.getStudent().getStudentId(),
                        enrollment.getSchoolYear()
                );

                enrollment.setDisciplinaryStatus(status);

                statusSaveCheckbox.setSelected(true);

                if (studentController != null) {
                    studentController.refreshTable();
                }

                return;
            }
        }

        statusSaveCheckbox.setSelected(false);

    }

}
