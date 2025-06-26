package Fourier.controller;

import Fourier.model.FourierModel;

/**
 * 1次元モデル用の具体的なコントローラクラス。
 * 共通機能は親クラスである FourierController から継承する。
 */
public class FourierController1D extends FourierController {
    
    public FourierController1D(FourierModel model) {
        // 親クラスのコンストラクタを呼び出す
        super(model);
    }
}