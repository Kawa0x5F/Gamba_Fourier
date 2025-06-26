package Fourier.controller;

import Fourier.model.FourierModel;

/**
 * 2次元モデル用の具体的なコントローラクラス。
 * 共通機能は親クラスである FourierController から継承する。
 */
public class FourierController2D extends FourierController {

    public FourierController2D(FourierModel model) {
        // 親クラスのコンストラクタを呼び出す
        super(model);
    }
}