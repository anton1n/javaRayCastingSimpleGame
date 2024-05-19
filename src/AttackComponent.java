public class AttackComponent implements Component {
    private boolean attacking;
    private long lastAttackTime;
    private long attackCooldown;
    private int attackPower;

    public AttackComponent(int attackPower, long attackCooldown) {
        this.attackPower = attackPower;
        this.attackCooldown = attackCooldown;
        this.lastAttackTime = System.currentTimeMillis();
        this.attacking = false;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public boolean isAttacking() {
        return attacking && canAttack();
    }

    private boolean canAttack() {
        long currentTime = System.currentTimeMillis();
        boolean canAttack = (currentTime - lastAttackTime) >= attackCooldown;
        if (canAttack && attacking) {
            lastAttackTime = currentTime;
        }
        return canAttack;
    }

    public void attack(HealthComponent target) {
        //System.out.println(canAttack());
        if (target != null && isAttacking()) {
            target.takeDamage(attackPower);
            attacking = false;
        }
    }
}
