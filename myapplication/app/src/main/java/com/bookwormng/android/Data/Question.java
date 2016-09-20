package com.bookwormng.android.Data;

/**
 * Created by Tofunmi Seun on 21/08/2015.
 */
public class Question {
    private String question;
    private String answer;
    private Long Id;
    //Constructor to create a new question.
    public Question(String newQuestion, String newAnswer, long Id)
    {
        this.question = newQuestion;
        this.answer = newAnswer;
        this.Id = Id;
    }
    //Getters.....
    public String getQuestion()
    {
        return this.question;
    }
    public String getAnswer()
    {
        return this.answer;
    }
    //Setters......
    public void setQuestion(String theQuestion)
    {
        this.question = theQuestion;
    }
    public void setAnswer(String theAnswer)
    {
        this.answer = theAnswer;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }
}
