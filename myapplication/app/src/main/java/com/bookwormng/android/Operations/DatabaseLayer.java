package com.bookwormng.android.Operations;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bookwormng.android.Data.Question;
import com.bookwormng.android.Data.QuestionDBHelper;
import com.bookwormng.android.Data.User;
import com.bookwormng.android.Data.UserDBHelper;

import java.util.ArrayList;

/**
 * Created by Tofunmi Seun on 03/09/2015.
 */
public class DatabaseLayer {
    static public void InitialDatabasePopulation(Context c)
    {
        ArrayList<Question> ques = new ArrayList<>();
        ques.add(new Question("Water is said to be at boiling point when vapour pressure is equal to ____ pressure?","Atmospheric",1));
        ques.add(new Question("A woman who Opts to breast feed another woman's baby is called a _____?"," Wet nurse",2));
        ques.add(new Question("Insects can walk on water due to a phenomenon called ___?","Surface Tension",3));
        ques.add(new Question("To be a good rapper which Nigerian Newspaper must you publish a line?","Punch",4));
        ques.add(new Question("All birds decided to work. Which bird bought a polish and became a shoemaker?","Kiwi",5));
        ques.add(new Question("Growth Hormones in plants are called ___?","Auxin",6));
        ques.add(new Question("When energy is in a dynamic form, it is described as ____?","Kinetic",7));
        ques.add(new Question("What action would you carry out on a matchstick and 'balance' ___?","Strike",8));
        ques.add(new Question("After sleeping, my computer is now in operation. It is UP and currently engages in what exercise?","Running",9));
        ques.add(new Question("What part of the cube gives us an advantage over other ___?","Edge",10));
        ques.add(new Question("Cosmetics and MAKE-up...Movie and MAKE believe.. Temporary and MAKE ___?","Shift",11));
        ques.add(new Question("Distressing and NERVE racking..Poison and NERVE gas.. Headquarters and NERVE ___?","Center",12));
        ques.add(new Question("Machine and BREAK down.. Relationship and BREAK up.. Speed and BREAK ____?","Neck",13));
        ques.add(new Question("Deception and WHITE lie.. Spectrum and WHITE light.. Termite and WHITE __?","Ant",14));
        ques.add(new Question("Rice and FOOD stuff..Illness and FOOD poisoning.. Pyramid and FOOD ___?","Chain",15));
        ques.add(new Question("Unwise and ILL-advised..Plane crash and ILL-fated.. dishonest person and ILL-__?","Gotten",16));
        ques.add(new Question("Consequence and AFTERmath..Overhead and AFTERnoon.. Death and AFTER__?","Life",17));
        ques.add(new Question("Vehicle and LANDrover.. Property and LANDlord.. Explosive and LAND__?","Mine",18));
        ques.add(new Question("Robin and blurred LINES..Geography and contour LINES.. Signature and ___ Lines?","Dotted",19));
        ques.add(new Question("Security and PASSword..Traveller and PASSport.. Jews and PASS__?","Over",20));
        ques.add(new Question("What do banks and social networks have in common _____?","Accounts",21));
        ques.add(new Question("What action is common to seat belt and a bolt _____?","Fasten",22));
        ques.add(new Question("What part of your body would you give to someone in a bid to help them ______?","Hand",23));
        ques.add(new Question("What word connects a mini microphone and a Cockroach _____?","Bug",24));
        ques.add(new Question("What word is common to electricity and an ocean _____?","Current",25));
        ques.add(new Question("What word does a newly presented book have in common with a rocket _____?","Launch",26));
        ques.add(new Question("What word does a circle have in common with a character in mortal kombat _____?","Sector",27));
        ques.add(new Question("What word does the severity of a situation have in common with angles _____?","Acute",28));
        ques.add(new Question("What is common to a car and a goat _____?","Horn",29));
        ques.add(new Question("What is common to a sentence and a contract agreement ______?","Clause",30));
        ques.add(new Question("1st and 2nd..... A and B..... Godliness and _____?","Cleanliness",31));
        ques.add(new Question("Earthly and terrestrial... Heavenly and _____?","Celestial",32));
        ques.add(new Question("Money and wallet... glasses and Pouch... Pistol and _____?","Holster",33));
        ques.add(new Question("Grade and C6... Carbon and C12... Explosive and _____?","C4",34));
        ques.add(new Question("Second and Time... Joule and Energy... Atmosphere and  _____?","Pressure",35));
        ques.add(new Question("Acid and Acidity.... Alkaline and Alkalinity... Salt and ______?","Salinity",36));
        ques.add(new Question("Enyimba and Aba... Heartland and Owerri.... El Kanemi and ______?","Maiduguri",37));
        ques.add(new Question("Benin and Oba.... Onitsha and Obi... Ife and ______?","Ooni",38));
        ques.add(new Question("Sokoto and Sultan.... Kano and Emir.... Borno and _______?","Shehu",39));
        ques.add(new Question("Igbo and Ohaneze.... Hausa and Arewa.... Yoruba and _______?","Afenifere",40));
        ques.add(new Question("He was held in contEMPT of the court after his attEMPT to escape. More so, he looked dirty and _____?","Unkempt",41));
        ques.add(new Question("Snake and pUANA.... Illegal and marijUANA.... Lizard and ______?","Iguana",42));
        ques.add(new Question("Buildings and eSTATE..... Position and reinSTATE...... Cancer and ______?","Prostate",43));
        ques.add(new Question("Fear and frIGHT..... Eye and sIGHT.... Tomato and ______?","Blight",44));
        ques.add(new Question("Physics and periSCOPE.... Lab and microSCOPE.... Astrology and _____?","Horoscope",45));
        ques.add(new Question("Disease and suscepTIBLE.... Car and converTIBLE.... Undeniable evidence and ______?","Incontrovertible",46));
        ques.add(new Question("PosTERITY will not forgive our leaders' ausTERITY measures except they do it with skill and ______?","Dexterity",47));
        ques.add(new Question("Indonesia and jakARTA.... Microsoft and encARTA.... Ancient Greece and ______?","Sparta",48));
        ques.add(new Question("Loyalty and pLEDGE.... Books and knowLEDGE... Hammer and ______?","Sledge",49));
        ques.add(new Question("Pride and concEIT.... Fake and counterfEIT... Temperature and ______?","Fahrenheit",50));
        ques.add(new Question("If you write \"H\" to the base of \"2\" in mathematics, in English, we call the \"2\" a ______?","Subscript",51));
        ques.add(new Question("Next Friday is Christmas. If the Thursday preceding it were a woman, her name would be ______?","Eve",52));
        ques.add(new Question("What animal would browse the internet in the fire ______?","Fox",53));
        ques.add(new Question("For you to imply that something makes no sense, it has to be the excrement of what animal ______?","Bull",54));
        ques.add(new Question("The sum of the first 5 perfect squares gives ______?","55",55));
        ques.add(new Question("In which Nigerian State would you find bridges, good roads, pipe borne water etc in the stomach of the indigenes ______?","Ekiti",56));
        ques.add(new Question("After all the animals committed the crime, which animal was singled out for blame ______?","Goat",57));
        ques.add(new Question("A mosquito carries the organism that causes malaria, hence it raps like which rapper ______?","Vector",58));
        ques.add(new Question("Which letter of the alphabet would you \"peg\" on a graphic image as a format for computer storage ______?","J",59));
        ques.add(new Question("He has the fastest goal in a world cup match. His name is ______?","Hakan Sukur",60));
        ques.add(new Question("Musa and refuse COLLECTOR... Matthew and tax COLLECTOR... Angelina Jolie and ______ COLLECTOR?","Bone",61));
        ques.add(new Question("Sleep and bedROOM... plant and mushROOM... photograph and ______ROOM?","Dark",62));
        ques.add(new Question("Plant and tapROOT... Math and squareROOT... Village and ______ROOT?","Grass",63));
        ques.add(new Question("Federer and GRAND slam... Court and GRAND jury... Motor racing and ______?","Prix",64));
        ques.add(new Question("Task and carry OUT... Discontinue and back OUT... Punishment and ______OUT?","mete",65));
        ques.add(new Question("Social group and MIDDLE class... life and MIDDLE age... Iran and MIDDLE ______?","East",66));
        ques.add(new Question("Beginning and OUTset... Military camp and OUTpost... Attitude and OUT______?","Set",67));
        ques.add(new Question("Clean and SPOTless... Exact and SPOT on... Celebrity and SPOT______?","Light",68));
        ques.add(new Question("Fashion and BANDwagon... Military and BANDmaster... \"signal processing\" and BAND______?","Width",69));
        ques.add(new Question("Arrangement and LAYout... Roadside and LAY-by... Lazy and LAY-______?","About",70));
        ques.add(new Question("What word does a camera have in common with a fast moving car _____?","Zoom",71));
        ques.add(new Question("What word does a mirror image have in common with a deep thought ______?","Reflection",72));
        User user = new UserDBHelper(c).CreateUserFromDatabase();
        QuestionDBHelper qdb = new QuestionDBHelper(c);
        for(Question item  : ques)
        {
                qdb.AddQuestion(item);
                if (ques.get(ques.size() - 1) == item)
                {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putLong("lastquestionindex", item.getId());
                    editor.commit();
                }
        }
        qdb.close();
    }
}