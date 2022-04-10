<?php return new class extends Migration {
    public function up()
    {
         Schema::table('users', function (\Illuminate\Database\Schema\Blueprint $table) {
             $table->string('new_column_1');
             $table->integer('new_column_2');
         });
    }

    public function down()
    {
        Schema::table('users', function (\Illuminate\Database\Schema\Blueprint $table) {
            $table->dropColumn(['<caret>']);
        });
    }
}
