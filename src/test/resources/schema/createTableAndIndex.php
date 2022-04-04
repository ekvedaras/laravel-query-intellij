<?php Schema::create('orders', function (\Illuminate\Database\Schema\Blueprint $table) {
      $table->id();
      $table->string('branch')->index();
      $table->integer('amount');
      $table->double('weight');
      $table->float('price');
      $table->timestamps();

      $table->index(['<caret>']);
  });
