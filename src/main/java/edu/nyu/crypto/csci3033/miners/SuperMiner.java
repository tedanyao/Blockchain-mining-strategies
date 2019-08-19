package edu.nyu.crypto.csci3033.miners;

import edu.nyu.crypto.csci3033.blockchain.Block;
import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;

public class SuperMiner extends BaseMiner implements Miner {
    private Block currentHead;
    private boolean selfishMode;
    private Block currentMiningBlock;

    public SuperMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);

    }

    @Override
    public Block currentlyMiningAt() {
        return currentMiningBlock;
    }

    @Override
    public Block currentHead() {
        // which is for broadcasting to others
        return currentHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        if(isMinerMe) {
            if (selfishMode) {
                if (block.getHeight() > currentMiningBlock.getHeight()) {
                    // store selfish blocks
                    this.currentMiningBlock = block;
                }
            }
            else {
                if (block.getHeight() > currentHead.getHeight()) {
                    this.currentHead = block;
                    this.currentMiningBlock = block;
                }
            }
        }
        else{
            if (currentHead == null || currentMiningBlock == null) {
                currentHead = block;
                currentMiningBlock = block;
            }
            else if (block != null) {
                if (selfishMode) {
                    if (block.getHeight() > currentMiningBlock.getHeight()) {
                        // no selfish blocks
                        this.currentMiningBlock = block;
                        this.currentHead = block;
                    }
                    else if (block.getHeight() >= this.currentMiningBlock.getHeight() - 1) {
                        // show your hided block
                        this.currentHead = this.currentMiningBlock;

                    }
                }
                else { // normal mode
                    if (currentMiningBlock != currentHead) {
                        currentHead = currentMiningBlock;
                    }
                    else if (block.getHeight() > currentHead.getHeight()) {
                        this.currentHead = block;
                        this.currentMiningBlock = block;
                    }
                }
            }
        }
    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {

        this.currentHead = genesis;
        this.currentMiningBlock = genesis;
        networkUpdate(networkStatistics);
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        double totalHashRate = statistics.getTotalHashRate();
        double myHashRate = getHashRate();
        double alpha = myHashRate/totalHashRate;
        //double myConn = getConnectivity();
        //double totalConn = statistics.getTotalConnectivity();
        double winProb = 1.0/2.0; // myConn/totalConn;
        // alpha^2 * (2 rewards) + alpha * (reward) * winProbability > alpha * (reward)
        // winProbability = am I the largest connectivity? Not sure, assume 1/2
        if (alpha * 2 + winProb > 1) {
            //System.out.println("true");
            this.selfishMode = true;
        }
        else {
            //System.out.println("false");
            this.selfishMode = true;
        }
    }
}
