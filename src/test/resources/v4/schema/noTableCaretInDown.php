<?php return new class extends Migration {
    public function up()
    {
         Schema::create('orders', function (\Illuminate\Database\Schema\Blueprint $table) {
         });
    }

    public function down()
    {
        Schema::table(, function (\Illuminate\Database\Schema\Blueprint $table) {
            $table->dropColumn('<caret>');
        });
    }
}
