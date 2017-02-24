/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagedownload;

import java.awt.event.KeyEvent;

/**
 *
 * @author wytsang
 */
public enum Keys {
    
    KEY_a('a', new int[]{KeyEvent.VK_A}),
    KEY_b('b', new int[]{KeyEvent.VK_B}),
    KEY_c('c', new int[]{KeyEvent.VK_C}),
    KEY_d('d', new int[]{KeyEvent.VK_D}),
    KEY_e('e', new int[]{KeyEvent.VK_E}),
    KEY_f('f', new int[]{KeyEvent.VK_F}),
    KEY_g('g', new int[]{KeyEvent.VK_G}),
    KEY_h('h', new int[]{KeyEvent.VK_H}),
    KEY_i('i', new int[]{KeyEvent.VK_I}),
    KEY_j('j', new int[]{KeyEvent.VK_J}),
    KEY_k('k', new int[]{KeyEvent.VK_K}),
    KEY_l('l', new int[]{KeyEvent.VK_L}),
    KEY_m('m', new int[]{KeyEvent.VK_M}),
    KEY_n('n', new int[]{KeyEvent.VK_N}),
    KEY_o('o', new int[]{KeyEvent.VK_O}),
    KEY_p('p', new int[]{KeyEvent.VK_P}),
    KEY_q('q', new int[]{KeyEvent.VK_Q}),
    KEY_r('r', new int[]{KeyEvent.VK_R}),
    KEY_s('s', new int[]{KeyEvent.VK_S}),
    KEY_t('t', new int[]{KeyEvent.VK_T}),
    KEY_u('u', new int[]{KeyEvent.VK_U}),
    KEY_v('v', new int[]{KeyEvent.VK_V}),
    KEY_w('w', new int[]{KeyEvent.VK_W}),
    KEY_x('x', new int[]{KeyEvent.VK_X}),
    KEY_y('y', new int[]{KeyEvent.VK_Y}),
    KEY_z('z', new int[]{KeyEvent.VK_Z}),
    KEY_0('0', new int[]{KeyEvent.VK_0}),
    KEY_1('1', new int[]{KeyEvent.VK_1}),
    KEY_2('2', new int[]{KeyEvent.VK_2}),
    KEY_3('3', new int[]{KeyEvent.VK_3}),
    KEY_4('4', new int[]{KeyEvent.VK_4}),
    KEY_5('5', new int[]{KeyEvent.VK_5}),
    KEY_6('6', new int[]{KeyEvent.VK_6}),
    KEY_7('7', new int[]{KeyEvent.VK_7}),
    KEY_8('8', new int[]{KeyEvent.VK_8}),
    KEY_9('9', new int[]{KeyEvent.VK_9}),
    KEY_BACK_SLASH('\\', new int[]{KeyEvent.VK_BACK_SLASH}),
    KEY_UNDERSCORE('_', new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS}),
    KEY_PERIOD('.', new int[]{KeyEvent.VK_PERIOD}),
    KEY_COLON(':', new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON});
    
    private char label;
    private int[] keys;
    
    Keys(char label, int[] keys){
        this.label= label;
        this.keys= keys;
    }

    public char getLabel() {
        return label;
    }

    public int[] getKeys() {
        return keys;
    }
    
    public static Keys lookup(char label){
        for(Keys k: Keys.values()){
            if(label==k.label){
                return k;
            }
        }
        return null;
    }
    
}
