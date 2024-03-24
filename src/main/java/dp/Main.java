package dp;

import com.gurobi.gurobi.*;

/* This example formulates and solves the following simple MIP model:
    maximize x + y + 2z
    subject to x + 2y + 3z <= 4
               x + y >= 1
             x, y, z binary
*/
public class Main
{
    public static void main(String[] args)
    {
        tryModel();
    }

    public static void tryModel()
    {
        try
        {
            // ------------------------------------------------------------------------------------------------
            // 1. Create empty environment, set options, and start
            // In this call we requested an empty environment, choose a log file, and started the environment.
            GRBEnv env = new GRBEnv(true);
            env.set("logFile", "mip1.log");
            env.start();

            // ------------------------------------------------------------------------------------------------
            // 2. Create empty model
            GRBModel model = new GRBModel(env);

            // ------------------------------------------------------------------------------------------------
            // 3.Create variables
            //                  [1: lb (optional): Lower bound for new variable,
            //                   2: ub (optional): Upper bound for new variable,
            //                   3: obj (optional): Objective coefficient for new variable
            //                   - zero here - we’ll set the objective later,
            //                   4: vtype (optional): Variable type for new variable (GRB.CONTINUOUS, GRB.BINARY,
            //                   GRB.INTEGER, GRB.SEMICONT, or GRB.SEMIINT),
            //                   5: name (optional): Name for new variable (stored as an ASCII string),
            //                   6: column (optional): Column object that indicates the set of constraints
            //                   in which the new variable participates, and the associated coefficients.]
            GRBVar x = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x");
            GRBVar y = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "y");
            GRBVar z = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "z");

            // ------------------------------------------------------------------------------------------------
            // 4. Set objective: maximize x + y + 2z
            // We build our objective by first constructing an empty linear expression and adding three terms to it.
            // AddTerm() - Add a single term into a linear expression.
            // [1: coeff: Coefficient for new term.
            //  2: var: Variable for new term.]
            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1.0, x);
            expr.addTerm(1.0, y);
            expr.addTerm(2.0, z);
            model.setObjective(expr, GRB.MAXIMIZE);
            // Model.setObjective(expr, sense=None) - Set the model objective equal to a linear or quadratic expression
            // [1: expr: New objective expression. Argument can be a linear or quadratic expression
            //           (an objective of type LinExpr or QuadExpr).
            // 2: sense (optional): Optimization sense (GRB.MINIMIZE for minimization, GRB.MAXIMIZE for maximization)]

            // ------------------------------------------------------------------------------------------------
            // 5. Add constraint : x + 2y + 3z <= 4
            expr = new GRBLinExpr();
            expr.addTerm(1.0, x);
            expr.addTerm(2.0, y);
            expr.addTerm(3.0, z);
            model.addConstr(expr, GRB.LESS_EQUAL, 4.0, "c0");
            // GRBModel.AddConstr() - Add a single linear constraint to a model.
            // [1: lhsExpr: Left-hand side expression for new linear constraint.
            //  2: sense: Sense for new linear constraint (GRB.LESS_EQUAL, GRB.EQUAL, or GRB.GREATER_EQUAL).
            //  3: rhsExpr: Right-hand side expression for new linear constraint.
            //  4: name: Name for new constraint.]

            // ------------------------------------------------------------------------------------------------
            // 6. Add constraint : x + y >= 1
            expr = new GRBLinExpr();
            expr.addTerm(1.0, x);
            expr.addTerm(1.0, y);
            model.addConstr(expr, GRB.GREATER_EQUAL, 1.0, "c1");

            // ------------------------------------------------------------------------------------------------
            // 7. Optimize model
            // GRBModel::optimize() - Optimize the model. The algorithm used for the optimization depends on the
            // model type (simplex or barrier for a continuous model; branch-and-cut for a MIP model).
            model.optimize();
            System.out.println(x.get(GRB.StringAttr.VarName) + " " + x.get(GRB.DoubleAttr.X));
            System.out.println(y.get(GRB.StringAttr.VarName) + " " + y.get(GRB.DoubleAttr.X));
            System.out.println(z.get(GRB.StringAttr.VarName) + " " + z.get(GRB.DoubleAttr.X));
            System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

            // ------------------------------------------------------------------------------------------------
            // 8. Dispose of model and environment
            model.dispose();

            env.dispose();
        }
        catch (GRBException e)
        {
            System.out.println("Error code : " + e.getErrorCode() + ". " + e.getMessage());
        }
    }
}