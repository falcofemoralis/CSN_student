package com.BSLCommunity.CSN_student.Presenters;

import com.BSLCommunity.CSN_student.Constants.GrantType;
import com.BSLCommunity.CSN_student.Constants.MarkErrorType;
import com.BSLCommunity.CSN_student.Constants.SubjectValue;
import com.BSLCommunity.CSN_student.Models.EditableSubject;
import com.BSLCommunity.CSN_student.Models.Subject;
import com.BSLCommunity.CSN_student.Models.SubjectModel;
import com.BSLCommunity.CSN_student.Models.UserData;
import com.BSLCommunity.CSN_student.ViewInterfaces.GradeCalculatorView;

import java.util.ArrayList;

public class GradeCalculatorPresenter {
    private final GradeCalculatorView gradeCalculatorView;
    private final SubjectModel subjectModel;
    private final double RATING_MULTIPLY = 0.9;
    private final double REGULAR_GRANT = 4.0;
    private final double HIGH_GRANT = 5.0;

    public GradeCalculatorPresenter(GradeCalculatorView gradeCalculatorView) {
        this.gradeCalculatorView = gradeCalculatorView;
        this.subjectModel = SubjectModel.getSubjectModel();
    }

    public void initSubjects() {
        ArrayList<EditableSubject> editableSubjects = UserData.getUserData().editableSubjects;
        ArrayList<Subject> subjects = subjectModel.subjects;
        int count = 0;

        if(editableSubjects != null) {
            for (EditableSubject editableSubject : editableSubjects) {
                for (Subject subject : subjects) {
                    if (subject.idSubject == editableSubject.idSubject && editableSubject.subjectValue != SubjectValue.TEST) {
                        count++;
                        gradeCalculatorView.setSubject(subject.name, editableSubject.subjectValue);
                        break;
                    }
                }
            }
            if(count > 0) {
                gradeCalculatorView.showSubjects();
            }
        }
    }

    public void calculateResult(ArrayList<String> marks) {
        int sum100 = 0, sum5 = 0, count = 0;
        boolean isGrant = true;

        for (String mark : marks) {
            if (!mark.equals("")) {
                int mark100 = Integer.parseInt(mark);

                int mark5;
                try {
                    mark5 = convert100ValueTo5(mark100);
                } catch (Exception e) {
                    e.printStackTrace();
                    gradeCalculatorView.showMsg(MarkErrorType.MORE100);
                    reset();
                    return;
                }

                if (mark100 >= 60) {
                    sum100 += mark100;
                    sum5 += mark5;
                    count++;

                    if (mark5 == 3) {
                        isGrant = false;
                    }
                } else {
                    gradeCalculatorView.showMsg(MarkErrorType.EXAM_FAILED);
                    reset();
                    return;
                }
            }

            if (count > 0) {
                float result100 = (float) sum100 / count;
                float result5 = (float) sum5 / count;
                gradeCalculatorView.showResult((float) (result100 * RATING_MULTIPLY), result5);

                if (!isGrant || result5 < REGULAR_GRANT) {
                    gradeCalculatorView.setGrant(GrantType.NO_GRANT);
                } else if (result5 < HIGH_GRANT) {
                    gradeCalculatorView.setGrant(GrantType.REGULAR_GRANT);
                } else {
                    gradeCalculatorView.setGrant(GrantType.HIGH_GRANT);
                }
            } else {
                reset();
            }
        }
    }

    public void reset() {
        gradeCalculatorView.showResult(0, 0);
        gradeCalculatorView.setGrant(GrantType.NO_GRANT);
    }

    public int convert100ValueTo5(float value) throws Exception {
        if (value < 60) {
            return 2;
        } else if (value < 75) {
            return 3;
        } else if (value < 90) {
            return 4;
        } else if (value <= 100) {
            return 5;
        } else {
            throw new Exception("more than 100");
        }
    }

    public String convert100ValueToLetter(float value) throws Exception {
        if (value < 35) {
            return "F";
        } else if (value < 60) {
            return "FX";
        } else if (value < 70) {
            return "E";
        } else if (value < 75) {
            return "D";
        } else if (value < 85) {
            return "C";
        } else if (value < 90) {
            return "B";
        } else if (value <= 100) {
            return "A";
        } else {
            throw new Exception("more than 100");
        }
    }
}
