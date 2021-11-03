package ui;

import model.Ledger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LedgerPanel extends JPanel implements ActionListener {
    private final Ledger ledger;

    public LedgerPanel(Ledger ledger) {
        this.ledger = ledger;
    }

    public void actionPerformed(ActionEvent e) {

    }



}
