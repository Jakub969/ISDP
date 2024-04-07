package main;

import com.gurobi.gurobi.*;
import mvp.View;

public class Main
{
    public static void main(String[] args) throws GRBException
    {
        new View();
       //MajerMinBusov.runModel1();
    }
}

// TODO vytvoriť len jedno GRBEnv, aj ho zničiť na konci (GRBEnv.dispose())
// TODO masívna refaktorizácia/prerábka