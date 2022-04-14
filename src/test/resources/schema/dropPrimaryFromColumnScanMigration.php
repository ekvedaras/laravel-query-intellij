<?php return new class extends Migration {
    public function up()
    {
         Schema::create('orders', function (\Illuminate\Database\Schema\Blueprint $table) {
             $table->string('new_column_1')->primary();
             $table->integer('new_column_2')->unique();
             $table->integer('new_column_3');

             $table->unique(['new_column_1', 'new_column_2'], 'unique_new_column');
             $table->index('new_column_3');
             $table->unique(['new_column_2', 'new_column_3']);
         });
    }

    public function down()
    {
        Schema::table('orders', function (\Illuminate\Database\Schema\Blueprint $table) {
            $table->dropPrimary('<caret>');
        });
    }
}
