<?php return new class extends Migration {
    public function up()
    {
         Schema::create('orders', function (\Illuminate\Database\Schema\Blueprint $table) {
             $table->string('new_column_1');
             $table->integer('new_column_2');
             $table->index(['<caret>']);
         });
    }

    public function down()
    {
        Schema::table('orders', function (\Illuminate\Database\Schema\Blueprint $table) {
            $table->integer('new_column_3');
        });
    }
}
