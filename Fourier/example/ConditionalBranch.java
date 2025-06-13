/**
 * 
 */

package Fourier.example;

import java.util.function.Supplier;

/**
 * 条件分岐を司るオブジェクト
 */
public class ConditionalBranch extends Object {

    /**
     * 条件を供給者として保持するフィールド
    */
    private Supplier<Boolean> condition = null;

    /**
     * 条件分岐のコンストラクタ
     * @param conditionPassage 条件を表すラムダ式
     */
    public ConditionalBranch(Supplier<Boolean> conditionPassage) {
        this.condition = conditionPassage;

        return;
    }

    /**
     * 条件分岐を行う
     * @param conditionPassage　自分の条件の評価が真の時に実行する条件ラムダ式
     * @return 論理積の審議
     */
    public Boolean and(Supplier<Boolean> conditionPassage) {
        Boolean aBoolean = this.condition.get();
        if(aBoolean) { aBoolean = conditionPassage.get(); } else { ; }

        return aBoolean;
    }

    /**
     * 条件分岐を行う
     * @param aCondition　条件を表すラムダ式
     * @param conditionPassage 上記の条件の評価が真の時に実行するラムダ式
     * @return 論理積の真偽
     */
    public static Boolean and(Supplier<Boolean> aCondition, Supplier<Boolean> conditionPassage) {
        Boolean aBoolean = new ConditionalBranch(aCondition).and(conditionPassage);

        return aBoolean;
    }


    /**
     * 条件分岐を行う
     * @param elsePassage　自分の条件の評価が偽の時に実行するラムダ式
     */
    public void ifElse(Runnable elsePassage) {
        this.ifThenElse(() -> { ; }, elsePassage);

        return;
    }

    /**
     * 条件分岐を行う
     * @param aCondition 条件を表すラムダ式
     * @param elsePassage 上記の条件の評価が真の時に実行するラムダ式
     */
    public static void ifElse(Supplier<Boolean> aCondition, Runnable elsePassage) {
        ConditionalBranch.ifThenElse(aCondition, () -> { ; }, elsePassage);
        
        return;
    }

    /**
     * 条件分岐を行う
     * @param aCondition 条件を表すラムダ式
     * @param thenPassage 上記の条件の評価が真の時に実行するラムダ式
     */
    public static void ifThen(Supplier<Boolean> aCondition, Runnable thenPassage) {
        ConditionalBranch.ifThenElse(aCondition, thenPassage, () -> { ; });

        return;
    }

    /**
     * 条件分岐を行う
     * @param thenPassage 自分の条件の評価が真の時に実行するラムダ式
     * @param elsePassage 自分の条件の評価が偽の時に実行するラムダ式
     */
    public void ifThenElse(Runnable thenPassage, Runnable elsePassage) {
        if(this.condition.get()) { thenPassage.run(); } else { elsePassage.run(); }

        return;
    }

    /**
     * 条件分岐を行う
     * @param aCondition 条件を表すラムダ式
     * @param thenPassage 上記の条件の評価が真の時に実行するラムダ式
     * @param elsePassage 上記の条件の評価が偽の時に実行するラムダ式
     */
    public static void ifThenElse(Supplier<Boolean> aCondition, Runnable thenPassage, Runnable elsePassage) {
        new ConditionalBranch(aCondition).ifThenElse(thenPassage, elsePassage);

        return;
    }

    /**
     * 条件分岐を行う
     * @param conditionPassage 自分の条件の評価が偽の時に実行するラムダ式
     * @return 論理和の真偽
     */
    public Boolean or(Supplier<Boolean> conditionPassage) {
        Boolean aBoolean = this.condition.get();
        if(aBoolean) { ; } else { aBoolean = conditionPassage.get(); }

        return aBoolean;
    }

    /**
     * 条件分岐を行う
     * @param aCondition　条件を表すラムダ式
     * @param conditionPassage　上記の条件の評価が偽の時に実行するラムダ式
     * @return 論理和の真偽
     */
    public static Boolean or(Supplier<Boolean> aCondition, Supplier<Boolean> conditionPassage) {
        Boolean aBoolean = new ConditionalBranch(aCondition).or(conditionPassage);

        return aBoolean;
    }
}