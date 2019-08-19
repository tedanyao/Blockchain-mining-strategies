package edu.nyu.crypto.csci3033.miners;

import edu.nyu.crypto.csci3033.blockchain.Block;
import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;

public class MajorityMiner extends BaseMiner implements Miner {
    private Block currentHead;
    private boolean isMajority;

    public MajorityMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);

    }

    @Override
    public Block currentlyMiningAt() {
        return currentHead;
    }

    @Override
    public Block currentHead() {
        return currentHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        if(isMinerMe) {
            if (block.getHeight() > currentHead.getHeight()) {
                this.currentHead = block;
            }
        }
        else {
            if (currentHead == null) {
                currentHead = block;
            } else if (!this.isMajority && block != null && block.getHeight() > currentHead.getHeight()) {
                // update to the recent block only when it is not majority
                // otherwise, keep mining yourself
                this.currentHead = block;

            }
        }
    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {

        this.currentHead = genesis;
        networkUpdate(networkStatistics);
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        double totalHashRate = statistics.getTotalHashRate();
        double myHashRate = this.getHashRate();
        if (myHashRate/totalHashRate > 0.5) {
            this.isMajority = true;
        }
        else {
            this.isMajority = false;
        }

    }
}
